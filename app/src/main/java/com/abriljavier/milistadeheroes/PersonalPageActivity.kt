package com.abriljavier.milistadeheroes

import BackgroundAdapter
import com.abriljavier.milistadeheroes.adapters.CharacterAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abriljavier.milistadeheroes.adapters.CustomAdapter
import com.abriljavier.milistadeheroes.dataclasses.AttributesPJ
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var toolbar: Toolbar
private lateinit var dialog: AlertDialog

class PersonalPageActivity : AppCompatActivity() {

    private lateinit var charactersRecyclerView: RecyclerView
    private lateinit var characterAdapter: CharacterAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val welcomeMsg = findViewById<TextView>(R.id.welcomeMsg)
        val userName = intent.getStringExtra("user_name")

        charactersRecyclerView = findViewById(R.id.recycleView)
        val gridLayoutManager = GridLayoutManager(this, 2)
        charactersRecyclerView.layoutManager = gridLayoutManager

        val dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("user_id", -1)
        val characters = dbHelper.getPersonajesByUserId(userId)
        for (c in characters){
            println(c.background)
        }
        if (characters.isEmpty()) {
            welcomeMsg.text =
                "Bienvenido $userName, todavía no tienes personajes, comienza creando uno"
        } else {
            welcomeMsg.text = "Bienvenido $userName, selecciona uno de tus personajes"
            characterAdapter = CharacterAdapter(characters) { character ->
                showCharacterDialog(character, this, userId, characterAdapter)
            }
            charactersRecyclerView.adapter = characterAdapter
            updateCharactersList(userId, this, characterAdapter)
        }

    }

    override fun onResume() {
        super.onResume()
        if(::characterAdapter.isInitialized) {
            updateCharactersList(userId, this, characterAdapter)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Create -> {
                val intent = Intent(this, CharacterCreationActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.Profile -> {
                val userName = intent.getStringExtra("user_name")
                val dbHelper = DatabaseHelper(this)
                val user = dbHelper.getUserByUsername(userName!!)

                val intent = Intent(this, ProfileActivity::class.java)

                intent.putExtra("user_id", user?.user_id)
                intent.putExtra("user_name", user?.username)
                intent.putExtra("user_pass", user?.password)
                intent.putExtra("user_pc_id", user?.pc_id)

                startActivity(intent)

                true
            }

            R.id.logOut -> {
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show()
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
private fun updateCharactersList(userId: Int, context: Context, characterAdapter: CharacterAdapter) {
    val dbHelper = DatabaseHelper(context)
    val characters = dbHelper.getPersonajesByUserId(userId)
    println(characters)
    characterAdapter.updateData(characters)
    characterAdapter.notifyDataSetChanged()
}

private fun showCharacterDialog(character: Personaje, context: Context, userId: Int, characterAdapter: CharacterAdapter) {
    val dbHelper = DatabaseHelper(context)
    val dialogView = LayoutInflater.from(context).inflate(R.layout.show_character_dialog, null)


    val listView: ListView = dialogView.findViewById(R.id.listView)
    val characterImage = dialogView.findViewById<ImageView>(R.id.infoImageView)
    val characterName = dialogView.findViewById<TextView>(R.id.charNameText)
    val charInfo = dialogView.findViewById<TextView>(R.id.textView17)
    val attributesText = formatAttributes(character.attributes)
    val charInfoStatsText: TextView = dialogView.findViewById(R.id.charInfoStatsText)
    charInfoStatsText.text = attributesText

    characterName.text = character.name
    charInfo.setText("${character.characterClass?.className} de nivel ${character.numLevel} ")
    val competenciesFormatted = character.competiences.joinToString().replace("[", "").replace("]", "").replace("\"", "")
    val dataToShow = listOf(
        "Competencias: $competenciesFormatted",
        "Alineamiento: ${character.selectedAlignment}",
        "Edad: ${character.age}",
        "Idiomas: ${character.languages}"
    )
    val adapter = CustomAdapter(context, dataToShow)
    listView.adapter = adapter

    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.setView(dialogView)
    val dialog = dialogBuilder.create()

    val nextPageBtn = dialogView.findViewById<Button>(R.id.nextPageBtn)
    nextPageBtn.setOnClickListener {
        dialog.dismiss()
        showSecondDialog(character, context, userId, characterAdapter)
    }

    val deleteBtn: Button = dialogView.findViewById(R.id.deleteBtn)
    deleteBtn.setOnClickListener {
        AlertDialog.Builder(context).apply {
            setTitle("Eliminar personaje")
            setMessage("¿Estás seguro de querer eliminar este personaje?")
            setPositiveButton("Eliminar") { dialogInterface, i ->
                if (character.pj_id?.let { it1 -> dbHelper.deleteCharacter(it1) } == true) {
                    Toast.makeText(
                        context, "Personaje eliminado", Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    updateCharactersList(userId, context, characterAdapter)
                } else {
                    Toast.makeText(
                        context,
                        "Error al eliminar personaje",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setNegativeButton("Cancelar", null)
        }.show()
    }

    val levelUpBtn: Button = dialogView.findViewById(R.id.levelUpBtn)
    levelUpBtn.setOnClickListener {
        val newLevel = character.numLevel!!.plus(1)

        dialog.dismiss()

        val intent = Intent(context, LevelUpActivity::class.java).apply {
            putExtra("characterId", character.pj_id)
            putExtra("newLevel", newLevel)
            putExtra("charClass", character.characterClass!!.classId!!)
        }

        context.startActivity(intent)

        dialog.dismiss()

    }
    dialog.show()
}

private fun showSecondDialog(character: Personaje, context: Context, userId: Int, characterAdapter: CharacterAdapter) {
    val secondDialogView = LayoutInflater.from(context).inflate(R.layout.second_show_character_dialog, null)
    val listView: ListView = secondDialogView.findViewById(R.id.bg_list)
    val background = character.background
    val detailsList = mutableListOf<String>()
    val nextDialogBtn2: Button = secondDialogView.findViewById(R.id.nextDialogBtn2)
    val previousDialogBtn2: Button = secondDialogView.findViewById(R.id.previousDialogBtn2)

    background?.traits?.flaws?.forEach { (key, value) ->
        detailsList.add("Defecto $key: $value")
    }
    background?.traits?.ideals?.forEach { (key, value) ->
        detailsList.add("Ideal $key: $value")
    }
    background?.traits?.links?.forEach { (key, value) ->
        detailsList.add("Vínculo $key: $value")
    }
    background?.traits?.personalityTraits?.forEach { (key, value) ->
        detailsList.add("Rasgo de personalidad $key: $value")
    }

    val adapter = BackgroundAdapter(context, detailsList)
    listView.adapter = adapter

    val secondDialogBuilder = AlertDialog.Builder(context)
    secondDialogBuilder.setView(secondDialogView)
    val secondDialog = secondDialogBuilder.create()

    nextDialogBtn2.setOnClickListener {
        secondDialog.dismiss()
        showThirdDialog(character, context, userId, characterAdapter)
    }

    previousDialogBtn2.setOnClickListener {
        secondDialog.dismiss()
        showCharacterDialog(character, context, userId, characterAdapter)
    }

    secondDialog.show()
}

private fun showThirdDialog(character: Personaje, context: Context,userId: Int, characterAdapter: CharacterAdapter) {
    val thirdDialogView = LayoutInflater.from(context).inflate(R.layout.third_show_character_dialog, null)
    val notesTextView: TextView = thirdDialogView.findViewById(R.id.notesDialogTextView)
    val previousDialogBtn3: Button = thirdDialogView.findViewById(R.id.previousDialogBtn3)

    notesTextView.text = character.notes ?: "No hay notas disponibles."

    val thirdDialogBuilder = AlertDialog.Builder(context)
    thirdDialogBuilder.setView(thirdDialogView)
    val thirdDialog = thirdDialogBuilder.create()

    previousDialogBtn3.setOnClickListener {
        thirdDialog.dismiss()
        showSecondDialog(character, context, userId, characterAdapter)
    }

    thirdDialog.show()
}

private fun formatAttributes(attributes: AttributesPJ?): String {
    if (attributes == null) {
        return "No hay estadísticas disponibles"
    }
    return """
        Fuerza: ${attributes.STR}
        Destreza: ${attributes.DEX}
        Constitución: ${attributes.CON}
        Inteligencia: ${attributes.INT}
        Sabiduría: ${attributes.WIS}
        Carisma: ${attributes.CHA}
    """.trimIndent()
}