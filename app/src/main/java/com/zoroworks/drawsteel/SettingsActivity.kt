package com.zoroworks.drawsteel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var characterNameInput: EditText
    private lateinit var classButton: Button
    private lateinit var ancestryButton: Button
    private lateinit var careerButton: Button
    private lateinit var saveSettingsButton: Button

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedClass: String = ""
    private var selectedAncestry: String = ""
    private var selectedCareer: String = ""
    private var recoveries: Int = 10
    private var maxStamina: Int = 10
    private var victories: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        hideSystemBars()

        // Set the Toolbar as ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Optional: Set up your toolbar title, navigation, etc.
        supportActionBar?.title = "Character Settings"

        // Enable the "Up" button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true) // Optional: Hide the title if you want

        characterNameInput = findViewById(R.id.character_name_input)
        classButton = findViewById(R.id.class_button)
        ancestryButton = findViewById(R.id.ancestry_button)
        careerButton = findViewById(R.id.career_button)
        saveSettingsButton = findViewById(R.id.save_settings_button)

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Limit character name length
        characterNameInput.filters = arrayOf(android.text.InputFilter.LengthFilter(16))
        characterNameInput.typeface =
            android.graphics.Typeface.create("Impact", android.graphics.Typeface.NORMAL)

        // Load settings when opening the page
        loadSettings()

        classButton.setOnClickListener {
            showClassDialog()
        }

        ancestryButton.setOnClickListener {
            showAncestryDialog()
        }

        saveSettingsButton.setOnClickListener {
            saveSettings()
        }

        careerButton.setOnClickListener {
            showCareerDialog()
        }
    }

    private fun loadSettings() {
        val characterName = sharedPreferences.getString("CharacterName", "") ?: ""
        selectedClass = sharedPreferences.getString("Class", "Censor") ?: "Censor"
        selectedAncestry = sharedPreferences.getString("Ancestry", "Human") ?: "Human"
        selectedCareer = sharedPreferences.getString("Career", "Agent") ?: "Agent"
        victories = sharedPreferences.getInt("Victories", 0)
        recoveries = sharedPreferences.getInt(
            "Recoveries",
            when (selectedClass) {
                "Censor" -> 12
                "Conduit" -> 8
                "Elementalist" -> 8
                "Fury" -> 10
                "Null" -> 8
                "Shadow" -> 8
                "Tactician" -> 10
                "Talent" -> 8
                "Troubadour" -> 8
                else -> 10
            }
        )
        maxStamina = getMaxStaminaForClass(selectedClass)

        characterNameInput.setText(characterName)
        classButton.text = selectedClass
        ancestryButton.text = selectedAncestry
        careerButton.text = selectedCareer

    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putString("CharacterName", characterNameInput.text.toString())
        editor.putString("Class", selectedClass)
        editor.putString("Ancestry", selectedAncestry)
        editor.putString("Career", selectedCareer)
        editor.putInt("MaxStamina", maxStamina)
        editor.putInt("Recoveries", recoveries)
        editor.putInt("Victories", victories)
        editor.apply()

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showClassDialog() {
        val classOptions = arrayOf(
            "Censor",
            "Conduit",
            "Elementalist",
            "Fury",
            "Null",
            "Shadow",
            "Tactician",
            "Talent",
            "Troubadour"
        )
        showCustomDialog("Select Class", classOptions) {
            selectedClass = it; classButton.text = it; maxStamina = getMaxStaminaForClass(it)
        }
    }

    private fun showAncestryDialog() {
        val ancestryOptions = arrayOf(
            "Devil",
            "Dragon Knight",
            "Dwarf",
            "Wode Elf",
            "High Elf",
            "Hakaan",
            "Human",
            "Memonek",
            "Orc",
            "Older",
            "Time Raider"
        )
        showCustomDialog("Select Ancestry", ancestryOptions) {
            selectedAncestry = it; ancestryButton.text = it
        }
    }

    private fun showCareerDialog() {
        val careerOptions = arrayOf(
            "Agent",
            "Aristocrat",
            "Artisan",
            "Beggar",
            "Criminal",
            "Disciple",
            "Explorer",
            "Farmer",
            "Gladiator",
            "Laborer",
            "Mage's Apprentice",
            "Performer",
            "Politician",
            "Sage",
            "Sailor",
            "Soldier",
            "Warden",
            "Watch Officer"
        )
        showCustomDialog("Select Career", careerOptions) {
            selectedCareer = it; careerButton.text = it
        }
    }

    private fun showCustomDialog(
        title: String,
        options: Array<String>,
        onSelect: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_selection, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val listView = dialogView.findViewById<ListView>(R.id.dialog_list)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)

        dialogTitle.text = title
        dialogTitle.typeface =
            android.graphics.Typeface.create("Impact", android.graphics.Typeface.NORMAL)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        listView.adapter = adapter

        cancelButton.visibility = View.VISIBLE // Make cancel button visible

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Optionally handle cancel button click
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            onSelect(options[position])
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getMaxStaminaForClass(className: String): Int {
        return when (className) {
            "Censor", "Fury", "Null", "Tactician" -> 21
            "Conduit", "Elementalist", "Shadow", "Talent", "Troubadour" -> 18
            else -> 10
        }
    }

    // Handle back button press in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Go back to the previous activity (MainMenu)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}
