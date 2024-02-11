package com.abriljavier.milistadeheroes.fragments

import android.media.MediaPlayer
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
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import org.w3c.dom.Text

private lateinit var personaje: Personaje

class CharacterCreationFragmentSixth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_sixth, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!

        val bondTextView = view.findViewById<TextView>(R.id.selectedBondText)
        val flawTextView = view.findViewById<TextView>(R.id.selectedFlawText)

        val linksList =
            personaje.background?.traits?.links?.map { it.key.toString() + ": " + it.value }
                .orEmpty()
        val flawsList =
            personaje.background?.traits?.flaws?.map { it.key.toString() + ": " + it.value }
                .orEmpty()

        val linkSpinner = view.findViewById<Spinner>(R.id.linkSpinner)
        val linkAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, linksList)
        linkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        linkSpinner.adapter = linkAdapter

        val flawsSpinner = view.findViewById<Spinner>(R.id.flawSpinner)
        val flawsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, flawsList)
        flawsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        flawsSpinner.adapter = flawsAdapter

        linkSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedLink = linksList[position]
                var selectedLinkSplit = selectedLink.split(":")
                val mapOfSelectedLinks = mapOf(selectedLinkSplit[0].toInt() to selectedLinkSplit[1])
                personaje.background?.traits?.links = mapOfSelectedLinks
                bondTextView.text = selectedLink
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedLink = linksList[0]
                var selectedLinkSplit = selectedLink.split(":")
                val mapOfSelectedLinks = mapOf(selectedLinkSplit[0].toInt() to selectedLinkSplit[1])
                personaje.background?.traits?.links = mapOfSelectedLinks
                bondTextView.text = selectedLink

            }
        }

        flawsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedFlaws = flawsList[position]
                var selectedFlawsSplit = selectedFlaws.split(":")
                val mapOfSelectedFlaws =
                    mapOf(selectedFlawsSplit[0].toInt() to selectedFlawsSplit[1])
                personaje.background?.traits?.flaws = mapOfSelectedFlaws
                flawTextView.text = selectedFlaws

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                val selectedFlaws = flawsList[0]
                var selectedFlawsSplit = selectedFlaws.split(":")
                val mapOfSelectedFlaws =
                    mapOf(selectedFlawsSplit[0].toInt() to selectedFlawsSplit[1])
                personaje.background?.traits?.flaws = mapOfSelectedFlaws
                flawTextView.text = selectedFlaws

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

        MediaPlayer.create(context, R.raw.pasar_pagina)?.apply {
            start()
            setOnCompletionListener { mp -> mp.release() }
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)?.addToBackStack(null)?.commit()
    }
}