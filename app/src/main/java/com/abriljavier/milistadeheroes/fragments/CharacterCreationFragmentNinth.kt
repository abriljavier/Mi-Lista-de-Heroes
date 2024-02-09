package com.abriljavier.milistadeheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.R

class CharacterCreationFragmentNinth : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_ninth, container, false)

        view.findViewById<Button>(R.id.forwardBtn).setOnClickListener {
            goToNextFragment()
        }

        return view
    }
    private fun goToNextFragment() {
        val nextFragment = CharacterCreationFragmentTenth()

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}