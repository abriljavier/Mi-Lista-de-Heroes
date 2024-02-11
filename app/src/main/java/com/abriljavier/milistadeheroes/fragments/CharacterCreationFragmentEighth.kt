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

class CharacterCreationFragmentEighth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_eighth, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!

        view.findViewById<Button>(R.id.forwardBtn231).setOnClickListener {
            var history = view.findViewById<EditText>(R.id.historyInput).text.toString()
            var age = view.findViewById<EditText>(R.id.ageInput).text.toString()

            personaje.history = history
            personaje.age = age
            goToNextFragment()

        }
        return view

    }


    private fun goToNextFragment() {

        val nextFragment = CharacterCreationFragmentNinth()

        val bundle = Bundle().apply {
            putSerializable("personaje_key", personaje)
        }
        nextFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)?.addToBackStack(null)?.commit()
    }
}
