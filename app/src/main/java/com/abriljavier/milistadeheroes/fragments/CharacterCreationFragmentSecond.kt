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
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.abriljavier.milistadeheroes.DatabaseHelper
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.AttributesPJ
import com.abriljavier.milistadeheroes.dataclasses.Classe
import com.abriljavier.milistadeheroes.dataclasses.Personaje

class CharacterCreationFragmentSecond : Fragment() {

    private lateinit var personaje: Personaje
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_second, container, false)

        personaje = (arguments?.getSerializable("personaje_key") as? Personaje)!!
        var selectedClasse: Classe? = null

        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.writableDatabase
        val classes = dbHelper.getAllClasses()
        val classesNames = classes.map { it.className ?: "Clase Desconocida" }
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, classesNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        val classesSpinner = view.findViewById<Spinner>(R.id.classesSpinner)
        classesSpinner.adapter = adapter
        val classIcon = view.findViewById<ImageView>(R.id.classIcon)

        //LOS BOTONES DE + Y -

        val decreaseButtonSTR = view.findViewById<Button>(R.id.decreaseButtonSTR)
        val increaseButtonSTR = view.findViewById<Button>(R.id.increaseButtonSTR)
        val decreaseButtonDEX = view.findViewById<Button>(R.id.decreaseButtonDEX)
        val increaseButtonDEX = view.findViewById<Button>(R.id.increaseButtonDEX)
        val decreaseButtonCON = view.findViewById<Button>(R.id.decreaseButtonCON)
        val increaseButtonCON = view.findViewById<Button>(R.id.increaseButtonCON)
        val decreaseButtonINT = view.findViewById<Button>(R.id.decreaseButtonINT)
        val increaseButtonINT = view.findViewById<Button>(R.id.increaseButtonINT)
        val decreaseButtonWIS = view.findViewById<Button>(R.id.decreaseButtonWIS)
        val increaseButtonWIS = view.findViewById<Button>(R.id.increaseButtonWIS)
        val decreaseButtonCHA = view.findViewById<Button>(R.id.decreaseButtonCHA)
        val increaseButtonCHA = view.findViewById<Button>(R.id.increaseButtonCHA)

        decreaseButtonSTR.setOnClickListener { onDecreaseClickFUE(it) }
        increaseButtonSTR.setOnClickListener { onIncreaseClickFUE(it) }
        decreaseButtonDEX.setOnClickListener { onDecreaseClickDEX(it) }
        increaseButtonDEX.setOnClickListener { onIncreaseClickDEX(it) }
        decreaseButtonCON.setOnClickListener { onDecreaseClickCON(it) }
        increaseButtonCON.setOnClickListener { onIncreaseClickCON(it) }
        decreaseButtonINT.setOnClickListener { onDecreaseClickINT(it) }
        increaseButtonINT.setOnClickListener { onIncreaseClickINT(it) }
        decreaseButtonWIS.setOnClickListener { onDecreaseClickWIS(it) }
        increaseButtonWIS.setOnClickListener { onIncreaseClickWIS(it) }
        decreaseButtonCHA.setOnClickListener { onDecreaseClickCHA(it) }
        increaseButtonCHA.setOnClickListener { onIncreaseClickCHA(it) }

        // LA IMAGEN QUE ACOMPAÑA AL SPINNER
        val classImageMap = mapOf<String, Int>(
            "Barbaro" to R.drawable.barbaro,
            "Bardo" to R.drawable.bardo,
            "Brujo" to R.drawable.brujo,
            "Clerigo" to R.drawable.clerigo,
            "Druida" to R.drawable.druida,
            "Explorador" to R.drawable.explorador,
            "Guerrero" to R.drawable.guerrero,
            "Hechicero" to R.drawable.hechicero,
            "Mago" to R.drawable.mago,
            "Monje" to R.drawable.monje,
            "Paladin" to R.drawable.paladin,
            "Picaro" to R.drawable.picaro,
        )

        // EL SPINNER

        classesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedClasse = classes[position]
                val className = selectedClasse?.className
                val imageResId = classImageMap[className] ?: R.drawable.barbaro
                classIcon.setImageResource(imageResId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedClasse = classes[0]
                classIcon.setImageResource(R.drawable.barbaro)
            }
        }

        view.findViewById<Button>(R.id.forwardBtn2).setOnClickListener {

            val strValue = view.findViewById<EditText>(R.id.attributeValueSTR)?.text.toString().toInt()
            val dexValue = view.findViewById<EditText>(R.id.attributeValueDEX)?.text.toString().toInt()
            val conValue = view.findViewById<EditText>(R.id.attributeValueCON)?.text.toString().toInt()
            val intValue = view.findViewById<EditText>(R.id.attributeValueINT)?.text.toString().toInt()
            val wisValue = view.findViewById<EditText>(R.id.attributeValueWIS)?.text.toString().toInt()
            val chaValue = view.findViewById<EditText>(R.id.attributeValueCHA)?.text.toString().toInt()
            personaje.attributes = AttributesPJ(
                STR = strValue,
                DEX = dexValue,
                CON = conValue,
                INT = intValue,
                WIS = wisValue,
                CHA = chaValue
            )
            personaje.characterClass = selectedClasse

            val bundle = Bundle().apply {
                putSerializable("personaje_key", personaje)
            }
            val nextFragment = CharacterCreationFragmentThird().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction().replace(R.id.frameLayout, nextFragment)
                .addToBackStack(null)
                .commit()

        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateAttributeViewsWithRaceBonus()
    }

    private fun updateAttributeViewsWithRaceBonus() {
        val attributes = personaje?.race?.attributes
        val strengthBonus = attributes?.STR ?: 0
        val editText0 = view?.findViewById<EditText>(R.id.attributeValueSTR)
        editText0?.setText("${10 + strengthBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView1 = view?.findViewById<TextView>(R.id.bonusValueSTR)
        val bonus1 = strengthBonus / 2
        bonusTextView1?.text = "+$bonus1"

        val dexBonus = attributes?.DEX ?: 0
        val editText1 = view?.findViewById<EditText>(R.id.attributeValueDEX)
        editText1?.setText("${10 + dexBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView2 = view?.findViewById<TextView>(R.id.bonusValueDEX)
        val bonus2 = dexBonus / 2
        bonusTextView2?.text = "+$bonus2"

        val conBonus = attributes?.CON ?: 0
        val editText2 = view?.findViewById<EditText>(R.id.attributeValueCON)
        editText2?.setText("${10 + conBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView3 = view?.findViewById<TextView>(R.id.bonusValueCON)
        val bonus3 = conBonus / 2
        bonusTextView3?.text = "+$bonus3"

        val wisBonus = attributes?.WIS ?: 0
        val editText3 = view?.findViewById<EditText>(R.id.attributeValueWIS)
        editText3?.setText("${10 + wisBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView4 = view?.findViewById<TextView>(R.id.bonusValueWIS)
        val bonus4 = wisBonus / 2
        bonusTextView4?.text = "+$bonus4"

        val intBonus = attributes?.INT ?: 0
        val editText4 = view?.findViewById<EditText>(R.id.attributeValueINT)
        editText4?.setText("${10 + intBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView5 = view?.findViewById<TextView>(R.id.bonusValueINT)
        val bonus5 = intBonus / 2
        bonusTextView5?.text = "+$bonus5"

        val carBonus = attributes?.CHA ?: 0
        val editText5 = view?.findViewById<EditText>(R.id.attributeValueCHA)
        editText5?.setText("${10 + carBonus}", TextView.BufferType.EDITABLE)
        val bonusTextView6 = view?.findViewById<TextView>(R.id.bonusValueCHA)
        val bonus6 = carBonus / 2
        bonusTextView6?.text = "+$bonus6"
    }

    private fun onIncreaseClickFUE(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueSTR)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueSTR)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickFUE(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueSTR)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueSTR)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onIncreaseClickDEX(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueDEX)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueDEX)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickDEX(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueDEX)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueDEX)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onIncreaseClickCON(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueCON)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueCON)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickCON(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueCON)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueCON)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onIncreaseClickINT(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueINT)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueINT)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickINT(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueINT)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueINT)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onIncreaseClickWIS(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueWIS)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueWIS)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickWIS(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueWIS)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueWIS)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onIncreaseClickCHA(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueCHA)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue < 20) {
            editText.setText((currentValue + 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueCHA)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }

    private fun onDecreaseClickCHA(view: View) {
        val parent = view.parent as ViewGroup
        val editText = parent.findViewById<EditText>(R.id.attributeValueCHA)
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        if (currentValue > 8) {
            editText.setText((currentValue - 1).toString())
        }
        val bonusTextView = parent.findViewById<TextView>(R.id.bonusValueCHA)
        val attributeValue = editText?.text.toString().toIntOrNull() ?: 10
        val bonus = (attributeValue - 10) / 2
        if (bonus > 0){
            bonusTextView?.text = "+$bonus"
        } else if (bonus == 0){
            bonusTextView?.text = "$bonus"
        } else {
            bonusTextView?.text = "-$bonus"
        }
    }


}