package com.abriljavier.milistadeheroes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import android.net.Uri


class CharacterAdapter(var characters: MutableList<Personaje>, private val onItemClick: (Personaje) -> Unit) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val characterName: TextView = view.findViewById(R.id.charName)
        val charInfo: TextView = view.findViewById(R.id.charInfo)
        val charImg: ImageView = view.findViewById(R.id.charImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.character_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characters[position]
        val context = holder.itemView.context
        holder.characterName.text = character.name
        holder.charInfo.text = "${character.characterClass?.className} de nivel ${character.numLevel}"
        if (!character.imageUri.isNullOrEmpty()) {
            holder.charImg.setImageURI(Uri.parse(character.imageUri))
        } else {
            val className = character.characterClass?.className!!.toLowerCase()
            val resourceName = className ?: "Default"
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.charImg.setImageResource(resourceId)
            } else {
                holder.charImg.setImageResource(R.drawable.default_avatar)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(character)
        }
    }

    fun updateData(newCharacters: List<Personaje>) {
        characters = newCharacters.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = characters.size
}

