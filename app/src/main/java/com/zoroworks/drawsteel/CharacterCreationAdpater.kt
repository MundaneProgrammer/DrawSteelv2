package com.zoroworks.drawsteel

import DatabaseHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CharacterCreationAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 7 // Number of fragments (Name, Ancestry, Culture, Career, etc.)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CharacterNameFragment()
            1 -> AncestryFragment()
//            2 -> CultureFragment()
//            3 -> CareerFragment()
//            4 -> ClassFragment()
//            5 -> KitFragment()
//            6 -> FreeStrikesFragment()
            else -> CharacterNameFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Name"
            1 -> "Ancestry"
            2 -> "Culture"
            3 -> "Career"
            4 -> "Class"
            5 -> "Kit"
            6 -> "Free Strikes"
            else -> ""
        }
    }
}

class CharacterNameFragment : Fragment() {

    private lateinit var characterNameEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_character_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterNameEditText = view.findViewById(R.id.characterNameEditText)
    }
}

class AncestryFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var ancestryOptions: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ancestry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        spinner = view.findViewById(R.id.ancestrySpinner)

        // Fetch data from the database
        ancestryOptions = dbHelper.getAllOptions(DatabaseHelper.TABLE_ANCESTRIES)

        // Set up the spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ancestryOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
