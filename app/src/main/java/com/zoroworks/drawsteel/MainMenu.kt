package com.zoroworks.drawsteel

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

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
        val heroesManuscriptButton: Button = findViewById(R.id.heroes_manuscript_button) // New button
        val monstersManuscriptButton: Button = findViewById(R.id.monsters_manuscript_button) // New button
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

        // Open PDF Viewer Dialog when Hero's Manuscript Button is clicked
        heroesManuscriptButton.setOnClickListener {
            val pdfDialog = PdfViewerDialog(this, "MCDM RPG Heroes Manuscript Backer Packet 2.pdf")
            pdfDialog.show()
        }

        // Open PDF Viewer Dialog when Hero's Manuscript Button is clicked
        monstersManuscriptButton.setOnClickListener {
            val pdfDialog = PdfViewerDialog(this, "MCDM_RPG_Monsters_Manuscript_Backer_Packet_2.pdf")
            pdfDialog.show()
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

    class PdfViewerDialog(context: Context, private val assetFileName: String) : Dialog(context) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_pdf)

            // Set dialog size explicitly
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            val pdfView: PDFView = findViewById(R.id.pdfView)
            val closeButton: Button = findViewById(R.id.closeButton)
            val pageNumberText: TextView = findViewById(R.id.pageNumberText) // TextView for page number

            try {
                val assetManager = context.assets
                val inputStream = assetManager.open(assetFileName)

                pdfView.fromStream(inputStream)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .onPageChange { page, _ ->
                        // Update page number text
                        pageNumberText.text = "Page ${page + 1}" // Adding 1 to make it 1-based index
                    }
                    .load()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            closeButton.setOnClickListener { dismiss() }
        }
    }





}


