package com.abriljavier.milistadeheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.DatabaseHelper
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Feature
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentTenth: Fragment() {

    private lateinit var personaje: Personaje

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_tenth, container, false)

        personaje = arguments?.getSerializable("personaje_key") as? Personaje ?: Personaje()
        val featuresContainer = view.findViewById<LinearLayout>(R.id.featuresContainer)

        // Asume que DatabaseHelper ya tiene implementado getFeaturesByClassIdAndLevel
        val dbHelper = DatabaseHelper(requireContext())
        val classId = personaje.characterClass?.classId ?: 0
        val level = personaje.numLevel ?: 1

        val characterFeatures = dbHelper.getFeaturesByClassIdAndLevel(classId, level)
        personaje.features.addAll(characterFeatures)

        for (feature in personaje.features) {
            val featureTextView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "${feature.name}: ${feature.description}"
            }

            featuresContainer.addView(featureTextView)
        }

        view.findViewById<Button>(R.id.forwardBtn445).setOnClickListener {
            goToNextFragment()
        }

        return view
    }


    private fun goToNextFragment() {

        println(personaje)

        val nextFragment = CharacterCreationFragmentEleventh()

        val bundle = Bundle().apply {
            putSerializable("personaje_key", personaje)
        }
        nextFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)?.addToBackStack(null)?.commit()
    }
}
