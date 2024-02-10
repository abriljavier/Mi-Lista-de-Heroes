package com.abriljavier.milistadeheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Ideal
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import com.abriljavier.milistadeheroes.dataclasses.Traits

private lateinit var personaje: Personaje

class CharacterCreationFragmentSixth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_fifth, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!


        val linksList = personaje.background?.traits?.links?.map { it.key.toString() + ": " + it.value }.orEmpty()
        val flawsList = personaje.background?.traits?.flaws?.map { it.key.toString() + ": " + it.value }.orEmpty()

        val linkSpinner = view.findViewById<Spinner>(R.id.linkSpinner)
        val linkAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, linksList)
        linkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        linkSpinner.adapter = linkAdapter

        val flawsSpinner = view.findViewById<Spinner>(R.id.flawSpinner)
        val flawsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, flawsList)
        flawsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        flawsSpinner.adapter = flawsAdapter

        linkSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedLink = linksList[position]
                personaje.selectedBonds = selectedLink

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedLink = linksList[0]
                personaje.selectedTrait = selectedLink
            }
        }

        flawsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedFlaws = flawsList[position]
                personaje.selectedFlaws = selectedFlaws
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                personaje.selectedFlaws = flawsList[0]
            }
        }


        view.findViewById<Button>(R.id.forwardBtn88).setOnClickListener {
            goToNextFragment()
        }

        return view
    }
    private fun goToNextFragment() {
        val nextFragment = CharacterCreationFragmentSeventh()

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