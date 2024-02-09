package com.abriljavier.milistadeheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.DatabaseHelper
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import com.abriljavier.milistadeheroes.dataclasses.Race

class CharacterCreationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_first, container, false)

        var selectedRace: Race? = null
        val helperTextView = view.findViewById<TextView>(R.id.helperTextView)

        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.writableDatabase
        val races = dbHelper.getAllRaces()
        val raceNames = races.map { it.name ?: "Raza Desconocida" }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, raceNames).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        val raceSpinner = view.findViewById<Spinner>(R.id.racesSpinner)
        raceSpinner.adapter = adapter
        raceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedRace = races[position]
                val attributes = selectedRace!!.attributes
                val attributesText = buildString {
                    if (attributes?.STR != 0) append("Fuerza: +${attributes?.STR}\n")
                    if (attributes?.DEX != 0) append("Destreza: +${attributes?.DEX}\n")
                    if (attributes?.CON != 0) append("Constitución: +${attributes?.CON}\n")
                    if (attributes?.INT != 0) append("Inteligencia: +${attributes?.INT}\n")
                    if (attributes?.WIS != 0) append("Sabiduría: +${attributes?.WIS}\n")
                    if (attributes?.CHA != 0) append("Carisma: +${attributes?.CHA}")
                }.trim()
                helperTextView.text = attributesText
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRace = races[0]
            }
        }

        view.findViewById<Button>(R.id.forwardBtn).setOnClickListener {
            val nameInput = view.findViewById<EditText>(R.id.charNameInput)
            val nameText = nameInput.text.toString()
            if (nameText.isEmpty()) {
                nameInput.error = "Debe elegir un nombre de usuario"
                return@setOnClickListener
            }
            val personaje = Personaje(name = nameText, race = selectedRace)

            val bundle = Bundle().apply {
                putSerializable("personaje_key", personaje)
            }

            val nextFragment = CharacterCreationFragmentSecond().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction().replace(R.id.frameLayout, nextFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }

}