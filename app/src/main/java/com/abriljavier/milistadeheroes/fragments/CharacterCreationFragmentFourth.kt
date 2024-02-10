package com.abriljavier.milistadeheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.DatabaseHelper
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Background
import com.abriljavier.milistadeheroes.dataclasses.Classe
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentFourth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_fourth, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!
        var selectedBg: Background? = null
        var bgdesc = view.findViewById<TextView>(R.id.bgdesc)

        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.writableDatabase
        val backgrounds = dbHelper.getAllBackgrounds()
        val classesNames = backgrounds.map { it.bgName ?: "Background Desconocido" }
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, classesNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        val backgroundsSpinner = view.findViewById<Spinner>(R.id.bgspinner)
        backgroundsSpinner.adapter = adapter

        backgroundsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedBg = backgrounds[position]
                bgdesc.text = backgrounds[position].bgName
                personaje.background = selectedBg
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBg = backgrounds[0]
                bgdesc.text = backgrounds[0].bgName
                personaje.background = selectedBg

            }
        }

        view.findViewById<Button>(R.id.forwardBtn9).setOnClickListener {
            goToNextFragment()
        }

        return view
    }

    private fun goToNextFragment() {

        val nextFragment = CharacterCreationFragmentFifth()

        val bundle = Bundle().apply {
            putSerializable("personaje_key", personaje)
        }
        nextFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}