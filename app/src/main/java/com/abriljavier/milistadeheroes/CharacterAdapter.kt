import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abriljavier.milistadeheroes.R
import com.abriljavier.milistadeheroes.dataclasses.Personaje
import android.net.Uri


class CharacterAdapter(private var characters: List<Personaje>, private val onItemClick: (Personaje) -> Unit) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

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
        holder.characterName.text = character.name
        holder.charInfo.text = "${character.characterClass?.className} de nivel ${character.numLevel} "
        if (!character.imageUri.isNullOrEmpty()) {
            holder.charImg.setImageURI(Uri.parse(character.imageUri))
        } else {
            holder.charImg.setImageResource(R.drawable.barbarian)
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(character)
        }
    }

    fun updateData(newCharacters: List<Personaje>) {
        characters = newCharacters
        notifyDataSetChanged()
    }

    override fun getItemCount() = characters.size
}

