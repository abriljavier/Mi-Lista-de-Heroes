package com.abriljavier.milistadeheroes.fragments

import android.content.Context
import android.content.Intent
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
import com.abriljavier.milistadeheroes.PersonalPageActivity
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentEleventh : Fragment() {

    private lateinit var personaje: Personaje

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_eleventh, container, false)

        personaje = arguments?.getSerializable("personaje_key") as? Personaje ?: Personaje()

        val sharedPref = activity?.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userId = sharedPref?.getInt("user_id", 0)

        val pgInput = view.findViewById<EditText>(R.id.pgInput)
        val rollTheDiceButton = view.findViewById<Button>(R.id.rollTheDice)
        val competenciasLayout = view.findViewById<LinearLayout>(R.id.competenciasLayout)

        val hitDie = personaje.characterClass?.hitDie ?: "1d6"
        val maxHp = hitDie.split("d").last().toInt()

        pgInput.setText(maxHp.toString())
        pgInput.isEnabled = false
        rollTheDiceButton.isEnabled = false

        val abilitiesProficiencies =
            personaje.characterClass?.abilitiesProficiencies?.split(", ")?.toSet() ?: emptySet()
        val competencies = personaje.background?.competencies?.split(", ")?.toSet() ?: emptySet()
        val combinedProficiencies = (abilitiesProficiencies + competencies).distinct()
        combinedProficiencies.forEach { proficiency ->
            val textView = TextView(context).apply {
                text = proficiency
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            competenciasLayout.addView(textView)
        }


        view.findViewById<Button>(R.id.finishBtn).setOnClickListener {

            personaje.hitPoints = maxHp
            personaje.competiences.add(combinedProficiencies.toString())


            val dbHelper = DatabaseHelper(requireContext())
            dbHelper.writableDatabase
            if (userId != null) {
                dbHelper.addPersonaje(personaje, userId)
            }
            val intent = Intent(context, PersonalPageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }

        return view
    }
}
