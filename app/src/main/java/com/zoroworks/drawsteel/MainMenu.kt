package com.zoroworks.drawsteel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenu : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        hideSystemBars()

        // Initialize buttons
        val viewCharactersButton: Button = findViewById(R.id.view_characters_button)
        val createCharacterButton: Button = findViewById(R.id.create_character_button)
        val settingsButton: Button = findViewById(R.id.settings_button)
        val exitButton: Button = findViewById(R.id.exit_button)

        // Set up button click listeners
        viewCharactersButton.setOnClickListener {
            // Start the View Characters Activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        createCharacterButton.setOnClickListener {
            //Start the Create Character Activity
            val intent = Intent(this, CreateCharacter::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            // Start the Settings Activity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            // Close the app
            finishAffinity() // Closes all activities in the app stack
            System.exit(0)   // Forces the app to completely shut down
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
