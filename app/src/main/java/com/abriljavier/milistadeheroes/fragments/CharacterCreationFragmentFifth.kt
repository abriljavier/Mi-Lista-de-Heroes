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
        val traitTextView = view.findViewById<TextView>(R.id.selectedTraitText)
        val idealTextView = view.findViewById<TextView>(R.id.selectedIdealText)


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
                var selectedTrait = selectedTraitValue.split(":")
                val mapOfSelectedTraits = mapOf(selectedTrait[0].toInt() to selectedTrait[1])
                personaje.background?.traits?.personalityTraits = mapOfSelectedTraits
                traitTextView.text = selectedTraitValue
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedTrait = personalityTraitsList[0]
                val mapOfSelectedTraits = mapOf(selectedTrait[0].toInt() to selectedTrait[1].toString())
                personaje.background?.traits?.personalityTraits = mapOfSelectedTraits
                traitTextView.text = selectedTrait

            }
        }

        idealSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedIdealValue = idealsList[position]
                var selectedIdeal = selectedIdealValue.split(":")
                val mapOfSelectedIdeals = mapOf(selectedIdeal[0].toInt() to selectedIdeal[1])
                personaje.background?.traits?.ideals = mapOfSelectedIdeals
                idealTextView.text = selectedIdealValue

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedIdealValue = idealsList[0]
                var selectedIdeal = selectedIdealValue.split(":")
                val mapOfSelectedIdeals = mapOf(selectedIdeal[0].toInt() to selectedIdeal[1])
                personaje.background?.traits?.ideals = mapOfSelectedIdeals
                idealTextView.text = selectedIdealValue

            }
        }


        view.findViewById<Button>(R.id.forwardBtn12).setOnClickListener {
            goToNextFragment()
        }

        return view
    }
    private fun goToNextFragment() {


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