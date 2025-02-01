package com.zoroworks.drawsteel

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2


const val STORAGE_PERMISSION_CODE = 101
// Define keys for SharedPreferences
private val PREFS_NAME = "Settings"
private var CURRENT_STAMINA_KEY = "currentStamina"
private val MAX_STAMINA_KEY = "MaxStamina"
private val CHARACTER_NAME = "CharacterName"
private val VICTORIES = "Victories"
private val ANCESTRY_KEY = "Ancestry"
private val CLASS_KEY = "Class"
private val CAREER_KEY = "Career"
private val HEROIC_RESOURCE = "HeroicResource"


class MainActivity : AppCompatActivity() {

    private lateinit var profileImageButton: ImageView
    private lateinit var staminaDialog: AlertDialog

    // Launch the gallery to select an image
    private val getImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Convert URI to Bitmap and set it to ImageView
            val bitmap = getBitmapFromUri(it)
            if (bitmap != null) {
                profileImageButton.setImageBitmap(bitmap) // Set the Bitmap to the ImageView
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
        supportActionBar?.setDisplayShowTitleEnabled(false) // Optional: Hide the title if you want

        onClickListeners()

        setSettings()



        // Find the ViewPager2
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // Set the adapter to the ViewPager2
        viewPager.adapter = ScreenAdapter(this)

        // Optionally, you can add an animation when swiping between screens
        // For example, setting a custom page transformer for a transition effect
        viewPager.setPageTransformer { page, position ->
            page.alpha = 1 - Math.abs(position)
            page.scaleX = 1 - 0.1f * Math.abs(position)
            page.scaleY = 1 - 0.1f * Math.abs(position)
        }

        profileImageButton = findViewById(R.id.profile_image_button)

        // Set click listener on the ImageView
        profileImageButton.setOnClickListener {
            // Check and request permission if needed
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                // Permission already granted
                getImageResult.launch("image/*")
            }
        }
    }

    // Convert the image URI to Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream) // Decode the InputStream into a Bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageResult.launch("image/*")
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Adapter to load the fragments/screens
    private inner class ScreenAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 3 // Example: 3 screens
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DetailsFragment()
                1 -> DetailsFragment2()
                else -> DetailsFragment3()
            }
        }
    }

    fun showAncestryInfo(view: View?) {
        AlertDialog.Builder(this)
            .setTitle("Ancestry Info")
            .setMessage("Ancestry represents the origin of your character, such as Human, Elf, Dwarf, etc.")
            .setPositiveButton("OK", null)
    }

    fun showClassInfo(view: View?) {
        AlertDialog.Builder(this)
            .setTitle("Class Info")
            .setMessage("Class defines your character's main abilities and role in the game, like Warrior, Mage, etc.")
            .setPositiveButton("OK", null)
            .show()
    }

    fun showCareerInfo(view: View?) {
        AlertDialog.Builder(this)
            .setTitle("Career Info")
            .setMessage("Career defines your character's profession or path, such as Adventurer, Merchant, etc.")
            .setPositiveButton("OK", null)
            .show()
    }

    fun showSubclassInfo(view: View?) {
        AlertDialog.Builder(this)
            .setTitle("Subclass Info")
            .setMessage("Subclass represents a special branch of your character's class, such as Berserker for a Warrior.")
            .setPositiveButton("OK", null)
            .show()
    }

    // Set settings in the UI
    fun setSettings() {
        val (currentStamina, maxStamina, characterName, victories, ancestry, selectedClass, career) = loadSettings()

        val currentStaminaTextView = findViewById<TextView>(R.id.current_stamina_value)
        val maxStaminaTextView = findViewById<TextView>(R.id.max_stamina_value)
        val characterNameTextView = findViewById<TextView>(R.id.character_name)
        val victoriesProgressBar = findViewById<ProgressBar>(R.id.victory_points_progress)
        val ancestryTextView = findViewById<TextView>(R.id.ancestry_value)
        val classTextView = findViewById<TextView>(R.id.class_value)
        val careerTextView = findViewById<TextView>(R.id.career_value)

        // Update UI with loaded values
        currentStaminaTextView.text = "$currentStamina"
        maxStaminaTextView.text = "$maxStamina"
        characterNameTextView.text = characterName
        victoriesProgressBar.progress = victories
        ancestryTextView.text = ancestry
        classTextView.text = selectedClass
        careerTextView.text = career
    }

    // Function to save settings to SharedPreferences
    fun saveStamina(currentStamina: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(CURRENT_STAMINA_KEY, currentStamina)
        editor.apply()  // Apply the changes
    }

    // Function to load settings from SharedPreferences
    fun loadSettings(): Quadruple<Int, Int, String, Int, String, String, String> {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Fetch values from SharedPreferences with defaults
        val currentStamina = sharedPreferences.getInt(CURRENT_STAMINA_KEY, 0) // Default to 0
        val maxStamina = sharedPreferences.getInt(MAX_STAMINA_KEY, 100) // Default to 100
        val victories = sharedPreferences.getInt(VICTORIES, 0) // Default to 0
        val characterName = sharedPreferences.getString(CHARACTER_NAME, "Character Name") ?: "Character Name"
        val ancestry = sharedPreferences.getString(ANCESTRY_KEY, "Human") ?: "Human"  // Default ancestry value
        val selectedClass = sharedPreferences.getString(CLASS_KEY, "Censor") ?: "Censor"  // Default class value
        val career = sharedPreferences.getString(CAREER_KEY, "Agent") ?: "Agent"  // Default career value

        // Return all the settings data
        return Quadruple(currentStamina, maxStamina, characterName, victories, ancestry, selectedClass, career)
    }

    // Define a data class to hold all the values (seven values in total)
    data class Quadruple<A, B, C, D, E, F, G>(
        val first: A, // currentStamina
        val second: B, // maxStamina
        val third: C, // characterName
        val fourth: D, // victories
        val fifth: E, // ancestry
        val sixth: F, // selectedClass
        val seventh: G  // career
    )

    fun openStaminaDialog() {
        val (currentStamina, maxStamina) = loadSettings()
        val windedPoint = maxStamina / 2
        val dyingPoint = 0 - windedPoint

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_stamina_adjust, null)
        val currentStaminaDisplay = dialogView.findViewById<TextView>(R.id.current_stamina_display)
        val staminaInput = dialogView.findViewById<EditText>(R.id.stamina_input)
        val maxStaminaDisplay = dialogView.findViewById<TextView>(R.id.max_stamina_value)
        val windedPointDisplay = dialogView.findViewById<TextView>(R.id.winded_point_value)
        val dyingPointDisplay = dialogView.findViewById<TextView>(R.id.dying_point_value)
        val increaseButton = dialogView.findViewById<Button>(R.id.increase_button)
        val decreaseButton = dialogView.findViewById<Button>(R.id.decrease_button)

        currentStaminaDisplay.text = "$currentStamina"
        maxStaminaDisplay.text = "$maxStamina"
        windedPointDisplay.text = "$windedPoint"
        dyingPointDisplay.text = "$dyingPoint"

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Adjust Stamina")
            .setCancelable(true)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        staminaDialog = builder
        staminaDialog.show()

        fun updateStamina(updatedStamina: Int) {
            if (updatedStamina in 0..maxStamina) {
                saveStamina(updatedStamina)
                setSettings()
                Toast.makeText(this, "Stamina Updated", Toast.LENGTH_SHORT).show()
                staminaDialog.dismiss()
            } else {
                Toast.makeText(this, "Invalid stamina value", Toast.LENGTH_SHORT).show()
            }
        }

        increaseButton.setOnClickListener {
            val inputValue = staminaInput.text.toString()
            if (inputValue.isNotEmpty()) {
                val value = inputValue.toInt()
                if (currentStamina + value <= maxStamina) {
                    updateStamina(currentStamina + value)
                } else {
                    Toast.makeText(this, "Max stamina reached", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            }
        }

        decreaseButton.setOnClickListener {
            val inputValue = staminaInput.text.toString()
            if (inputValue.isNotEmpty()) {
                val value = inputValue.toInt()
                if (currentStamina - value >= 0) {
                    updateStamina(currentStamina - value)
                } else {
                    Toast.makeText(this, "Stamina can't be less than 0", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog(title: String, description: String) {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)

        // Set the title and description dynamically
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.dialog_description)
        descriptionTextView.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
        val button = dialogView.findViewById<Button>(R.id.dialog_button)

        titleTextView.text = title
        descriptionTextView.text = description

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Handle button click
        button.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun onClickListeners(){

        val victoriesTextView: TextView = findViewById(R.id.victoriesTextView)
        val staminaLayout: LinearLayout = findViewById(R.id.staminaLayout)
        val progressBar: ProgressBar = findViewById(R.id.victory_points_progress)


        // Set click listener for Armor TextView
        victoriesTextView.setOnClickListener {
            showDialog(
                "Victories",
                """
        Victories measure your hero’s increasing power over the course of an adventure, as they overcome battles and other challenges. At the start of an adventure, your hero has 0 Victories.<br><br>
        <b>VICTORIES FOR COMBAT</b><br>
        Each time your hero survives a combat encounter in which the party’s objectives are achieved, your Victories increase by 1. The Director can decide that a trivially easy encounter doesn’t increase a hero’s Victories.<br><br>
        <b>VICTORIES FOR NONCOMBAT CHALLENGES</b><br>
        When your hero successfully overcomes a big challenge that doesn’t involve combat, the Director can award you 1 Victory. Such challenges can include things like a particularly complicated and deadly trap, a negotiation, a montage test, a complicated puzzle, or the execution of a clever idea that avoids a battle.<br><br>
        <b>VICTORIES RESET</b><br>
        Whenever you finish a respite (see Respite), your Victories are converted into Experience.
        """
            )
        }

        staminaLayout.setOnClickListener {
            openStaminaDialog()
        }

        // Set the click listener for the ProgressBar
        progressBar.setOnClickListener {
            // Create the dialog
            val dialogView = layoutInflater.inflate(R.layout.dialog_progress_layout, null)

            val progressBarDialog: ProgressBar = dialogView.findViewById(R.id.dialog_progress_bar)
            val btnMinus: Button = dialogView.findViewById(R.id.btn_minus)
            val btnPlus: Button = dialogView.findViewById(R.id.btn_plus)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_cancel)
            val victoriesTextView: TextView = dialogView.findViewById(R.id.victories_value)
            val btnReset: Button = dialogView.findViewById(R.id.btn_reset)
            val btnLevelUp: Button = dialogView.findViewById(R.id.btn_level_up)

            // Create the dialog before setting listeners for the buttons
            val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)

            val dialog = builder.create()

            // Initialize the TextView with the current value of progress
            victoriesTextView.text = "Victories: ${progressBar.progress}"

            // Set the progress to match the original progress
            progressBarDialog.progress = progressBar.progress

            // Reset Button logic with confirmation popup
            btnReset.setOnClickListener {
                // Create a confirmation dialog
                val resetConfirmationDialog = AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("Do you really want to reset the progress?")
                    .setPositiveButton("Yes") { _, _ ->
                        progressBarDialog.progress = 0
                        victoriesTextView.text = "Victories: 0"
                        updateVictories(0)  // Reset the victories value in SharedPreferences
                        btnLevelUp.isEnabled = false  // Disable level up until full
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()  // Dismiss the confirmation dialog without resetting
                    }
                    .create()

                resetConfirmationDialog.show()  // Show the confirmation dialog
            }

            // Level Up Button logic
            btnLevelUp.isEnabled = progressBarDialog.progress == progressBarDialog.max  // Disable until full progress

            // Level Up logic
            btnLevelUp.setOnClickListener {
                // Handle level-up logic, such as increasing max progress, etc.
                // Example: increase the max progress by 5
                progressBarDialog.max += 5
                progressBarDialog.progress = 0  // Reset progress
                victoriesTextView.text = "Victories: 0"  // Reset the victories
                updateVictories(0)  // Update SharedPreferences after level up
            }

            // Handle Minus Button click
            btnMinus.setOnClickListener {
                if (progressBarDialog.progress > 0) {
                    progressBarDialog.progress -= 1
                    victoriesTextView.text = "Victories: ${progressBarDialog.progress}"  // Update TextView dynamically
                    updateVictories(progressBarDialog.progress)  // Update SharedPreferences
                    dialog.dismiss()  // Close the dialog after change
                }
            }

            // Handle Plus Button click
            btnPlus.setOnClickListener {
                if (progressBarDialog.progress < progressBarDialog.max) {
                    progressBarDialog.progress += 1
                    victoriesTextView.text = "Victories: ${progressBarDialog.progress}"  // Update TextView dynamically
                    updateVictories(progressBarDialog.progress)  // Update SharedPreferences
                    dialog.dismiss()  // Close the dialog after change
                }
            }



            // Handle Cancel Button click
            btnCancel.setOnClickListener {
                dialog.dismiss()  // Close the dialog without changes
            }

            // Show the dialog
            dialog.show()
        }





    }
    // Function to save the updated victories to SharedPreferences
    private fun updateVictories(newVictories: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(VICTORIES, newVictories)
        editor.apply()  // Apply the changes
        setSettings()  // Update UI with new victories value
    }

    // Handle back button press in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Go back to the previous activity (MainMenu)
            return true
        }
        return super.onOptionsItemSelected(item)
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
