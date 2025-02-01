package com.zoroworks.drawsteel

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2


private val PREFS_NAME = "Settings"
private var selectedClass: String = "Class"
private var selectedAncestry: String = "Ancestry"
private var recoveries: String = "Recoveries"
private var HEROIC_RESOURCE: String = "Heroic Resource"
private var SURGES: String = "Surges"

class DetailsFragment : Fragment(R.layout.fragment_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the TextView for Attributes
        val mightTextView: TextView = view.findViewById(R.id.mightTextView)
        val agilityTextView: TextView = view.findViewById(R.id.agilityTextView)
        val reasonTextView: TextView = view.findViewById(R.id.reasonTextView)
        val intuitionTextView: TextView = view.findViewById(R.id.intuitionTextView)
        val presenceTextView: TextView = view.findViewById(R.id.presenceTextView)



        setSettings()

        onClickListeners(
            mightTextView,
            agilityTextView,
            reasonTextView,
            intuitionTextView,
            presenceTextView
        )


    }

    private fun onClickListeners(
        mightTextView: TextView,
        agilityTextView: TextView,
        reasonTextView: TextView,
        intuitionTextView: TextView,
        presenceTextView: TextView
    ) {
        // Set an onClickListener for "Might"
        mightTextView.setOnClickListener {
            showDialog(
                "Might",
                "Might (M) represents strength and brawn. A creature’s ability to break down doors, swing an axe, stand up during an earthquake, or hurl an ally across a chasm is determined by Might"
            )
        }
        // Set an onClickListener for "Might"
        agilityTextView.setOnClickListener {
            showDialog(
                "Agility",
                "Agility (A) represents coordination and nimbleness. A creature’s ability to backflip out of danger, shoot a crossbow, dodge an explosion, or pluck keys from a guard’s belt is determined by Agility."
            )
        }
        // Set an onClickListener for "Might"
        reasonTextView.setOnClickListener {
            showDialog(
                "Reason",
                "Reason (R) represents a logical mind and education. A creature’s ability to solve a puzzle that unlocks a door, recall lore about necromancy, decipher a coded message, or blast a foe with psionic power is determined by Reason."
            )
        }
        // Set an onClickListener for "Might"
        intuitionTextView.setOnClickListener {
            showDialog(
                "Intuition",
                "Intuition (I) represents instincts and experience. A creature’s ability to recognize a faint sound as the approach of a distant rider, quickly read the tell of a bluffing gambler, calm a rearing horse, or track a monster across the tundra is determined by Intuition."
            )
        }
        // Set an onClickListener for "Might"
        presenceTextView.setOnClickListener {
            showDialog(
                "Presence",
                "Presence (P) represents force of personality. A creature’s ability to lie to a judge, convince a crowd to join a revolution, impress a queen at a royal banquet, or cast a magic spell by singing a song is determined by Presence."
            )
        }

    }

    private fun showDialog(title: String, description: String) {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)

        // Set the title and description dynamically
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.dialog_description)
        val button = dialogView.findViewById<Button>(R.id.dialog_button)

        titleTextView.text = title
        descriptionTextView.text = description

        // Build the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Handle button click
        button.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    fun setSettings() {
        val (heroicResourceName, heroicResource, recoveries, surges) = loadSettings()

        val rootView = requireView() // Ensure we have the fragment's view

        val heroicResourceDescTextView =
            rootView.findViewById<TextView>(R.id.heroicResourceDescTextView)
        val heroicResourceTextView = rootView.findViewById<TextView>(R.id.heroicResourceTextView)
        val recoveriesTextView = rootView.findViewById<TextView>(R.id.recoveriesTextView)
        val surgesTextView = rootView.findViewById<TextView>(R.id.surgesTextView)

        // Now you can set values to these TextViews
        heroicResourceDescTextView.text = heroicResourceName
        heroicResourceTextView.text = heroicResource.toString()
        recoveriesTextView.text = recoveries.toString()
        surgesTextView.text = surges.toString()
    }

    fun loadSettings(): Quadruple<String, Int, Int, Int> {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val selectedClass = sharedPreferences.getString(selectedClass, "Censor") ?: "Censor"
        val heroicResourceName = when (selectedClass) {
            "Censor" -> "Wrath"
            "Conduit" -> "Piety"
            "Elementalist" -> "Essence"
            "Fury" -> "Rage"
            "Null" -> "Discipline"
            "Shadow" -> "Insight"
            "Tactician" -> "Focus"
            "Talent" -> "Clarity"
            "Troubadour" -> "Drama"
            else -> "Heroic Power" // Default fallback in case of an unexpected class
        }
        val heroicResource = sharedPreferences.getInt(HEROIC_RESOURCE, 0)
        val surges = sharedPreferences.getInt(SURGES, 0)

        val recoveries = sharedPreferences.getInt(
            recoveries, when (selectedClass) {
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

        return Quadruple(heroicResourceName, heroicResource, recoveries, surges)
    }

    // Define a data class to hold four values (for the return from loadSettings())
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

class DetailsFragment2 : Fragment(R.layout.fragment_details2) {

}

class DetailsFragment3 : Fragment(R.layout.fragment_details3) {

}