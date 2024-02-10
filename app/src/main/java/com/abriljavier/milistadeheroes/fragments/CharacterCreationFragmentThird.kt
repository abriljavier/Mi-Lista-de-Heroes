package com.abriljavier.milistadeheroes.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var personaje: Personaje

class CharacterCreationFragmentThird : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_character_creation_third, container, false)

        personaje = arguments?.getSerializable("personaje_key") as? Personaje ?: Personaje()

        var uploadImg = view.findViewById<Button>(R.id.selectImg)
        uploadImg.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            }
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 102)
        }



        view.findViewById<Button>(R.id.forwardBtn3).setOnClickListener {
            goToNextFragment()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageView = view?.findViewById<ImageView>(R.id.avatarImageView)
                imageView?.setImageURI(uri)

                personaje.imageUri = uri.toString()
            }
        }
    }

    private fun goToNextFragment() {
        val bundle = Bundle().apply {
            putSerializable("personaje_key", personaje)
        }
        val nextFragment = CharacterCreationFragmentFourth().apply {
            arguments = bundle
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayout, nextFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
