package com.zoroworks.drawsteel

import DatabaseHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

class CreateCharacter : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creation)

        dbHelper = DatabaseHelper(this)
        viewPager = findViewById(R.id.viewPager)

        // Create a ViewPagerAdapter to display fragments
        val adapter = CharacterCreationAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }
}