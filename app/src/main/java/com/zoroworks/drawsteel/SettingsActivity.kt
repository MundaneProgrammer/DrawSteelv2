package com.zoroworks.drawsteel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var characterNameInput: EditText
    private lateinit var maxStaminaInput: EditText
    //private lateinit var heroicResourceSpinner: Spinner
    private lateinit var saveSettingsButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        characterNameInput = findViewById(R.id.character_name_input)
        maxStaminaInput = findViewById(R.id.max_stamina_input)
        //heroicResourceSpinner = findViewById(R.id.heroic_resource_spinner)
        saveSettingsButton = findViewById(R.id.save_settings_button)

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Heroic Resource Options
//        val heroicResources = arrayOf("Discipline", "Energy", "Rage", "Focus")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, heroicResources)
//        heroicResourceSpinner.adapter = adapter

        // Load settings when opening the page
        loadSettings()

        // Save settings when clicking the save button
        saveSettingsButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val characterName = sharedPreferences.getString("CharacterName", "") ?: ""
        val maxStamina = sharedPreferences.getInt("MaxStamina", 10).toString()
        //val heroicResourceIndex = sharedPreferences.getInt("HeroicResource", 0)

        characterNameInput.setText(characterName)
        maxStaminaInput.setText(maxStamina)
        //heroicResourceSpinner.setSelection(heroicResourceIndex)
    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putString("CharacterName", characterNameInput.text.toString())
        editor.putInt("MaxStamina", maxStaminaInput.text.toString().toIntOrNull() ?: 10)
        //editor.putInt("HeroicResource", heroicResourceSpinner.selectedItemPosition)
        editor.apply()

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
        finish()  // Close the activity
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }
}
