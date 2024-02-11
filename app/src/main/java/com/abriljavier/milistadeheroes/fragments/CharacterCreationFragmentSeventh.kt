package com.abriljavier.milistadeheroes.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentSeventh : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_seventh, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!

        val alignmentList = listOf(
            "Legal Bueno",
            "Neutral Bueno",
            "Caótico Bueno",
            "Legal Neutral",
            "Neutral",
            "Caótico Neutral",
            "Legal Malo",
            "Neutral Malo",
            "Caótico Malo"
        )

        val alignmentSpinner = view.findViewById<Spinner>(R.id.aligmentSpinner)
        val alignmentAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, alignmentList)
        alignmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alignmentSpinner.adapter = alignmentAdapter

        alignmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedAlignment = alignmentList[position]
                personaje.selectedAlignment = selectedAlignment

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedLink = alignmentList[0]
//                personaje.selectedTrait = selectedLink
            }
        }


        view.findViewById<Button>(R.id.forwardBtn123).setOnClickListener {
            var appearance = view.findViewById<EditText>(R.id.appearance).text.toString()
            if (appearance.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, ingrese la apariencia del personaje.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                personaje.appearance = appearance
                goToNextFragment()

            }

        }

        return view
    }

    private fun goToNextFragment() {


        val nextFragment = CharacterCreationFragmentEighth()

        val bundle = Bundle().apply {
            putSerializable("personaje_key", personaje)
        }
        nextFragment.arguments = bundle

        MediaPlayer.create(context, R.raw.pasar_pagina)?.apply {
            start()
            setOnCompletionListener { mp -> mp.release() }
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)?.addToBackStack(null)?.commit()
    }
}