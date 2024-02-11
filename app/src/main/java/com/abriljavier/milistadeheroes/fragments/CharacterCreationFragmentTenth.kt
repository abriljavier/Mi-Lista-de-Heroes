package com.abriljavier.milistadeheroes.fragments

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.DatabaseHelper
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentTenth: Fragment() {

    private lateinit var personaje: Personaje

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_tenth, container, false)

        personaje = arguments?.getSerializable("personaje_key") as? Personaje ?: Personaje()
        val featuresContainer = view.findViewById<LinearLayout>(R.id.featuresContainer)

        val dbHelper = DatabaseHelper(requireContext())
        val classId = personaje.characterClass?.classId ?: 0
        val level = personaje.numLevel ?: 1

        val characterFeatures = dbHelper.getFeaturesByClasseAndLevel(classId, level)

        val scrollView = ScrollView(requireContext())
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL

        for (feature in characterFeatures) {
            val featureTextView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(20, 0, 20, 20)
                }
                val featureText = "${feature.featureName}: ${feature.description}"

                val spannableString = SpannableString(featureText)
                val colonIndex = featureText.indexOf(':')
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    colonIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                text = spannableString
                setTextSize(20f) // Tamaño del texto en 20dp
            }

            linearLayout.addView(featureTextView)
        }

        scrollView.addView(linearLayout)
        featuresContainer.addView(scrollView)

        view.findViewById<Button>(R.id.forwardBtn445).setOnClickListener {
            goToNextFragment()
        }

        return view

    }


    private fun goToNextFragment() {


        val nextFragment = CharacterCreationFragmentEleventh()

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
