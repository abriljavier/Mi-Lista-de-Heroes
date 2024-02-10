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

class CharacterCreationFragmentFifth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_fifth, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!


        val personalityTraitsList = personaje.background?.traits?.personalityTraits?.map { it.key.toString() + ": " + it.value }.orEmpty()
        val idealsList = personaje.background?.traits?.ideals?.map { it.key.toString() + ": " + it.value }.orEmpty()

        val traitSpinner = view.findViewById<Spinner>(R.id.traitSpinner)
        val traitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, personalityTraitsList)
        traitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        traitSpinner.adapter = traitAdapter

        val idealSpinner = view.findViewById<Spinner>(R.id.idealSpinner)
        val idealAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, idealsList)
        idealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        idealSpinner.adapter = idealAdapter

        traitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedTraitValue = personalityTraitsList[position]
                personaje.selectedTrait = selectedTraitValue

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedTraitValue = personalityTraitsList[0]
                personaje.selectedTrait = selectedTraitValue
            }
        }

        idealSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedIdealValue = idealsList[position]
                println(selectedIdealValue)
                personaje.selectedIdeal = selectedIdealValue
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                personaje.selectedIdeal = idealsList[0]
            }
        }


        view.findViewById<Button>(R.id.forwardBtn12).setOnClickListener {
            goToNextFragment()
        }

        return view
    }
    private fun goToNextFragment() {

        println(personaje)

        val nextFragment = CharacterCreationFragmentSixth()

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