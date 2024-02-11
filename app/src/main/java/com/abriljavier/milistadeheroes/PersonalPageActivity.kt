package com.abriljavier.milistadeheroes

import BackgroundAdapter
import com.abriljavier.milistadeheroes.adapters.CharacterAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abriljavier.milistadeheroes.adapters.CustomAdapter
import com.abriljavier.milistadeheroes.dataclasses.AttributesPJ
import com.abriljavier.milistadeheroes.dataclasses.Personaje

class PersonalPageActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var dialog: AlertDialog
    private lateinit var charactersRecyclerView: RecyclerView
    private var userId: Int = -1
    private lateinit var characterAdapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name") ?: "Usuario"

        charactersRecyclerView = findViewById<RecyclerView>(R.id.recycleView).apply {
            layoutManager = GridLayoutManager(this@PersonalPageActivity, 2)
        }

        characterAdapter = CharacterAdapter(mutableListOf()) { character ->
            showCharacterDialog(character, this, userId)
        }
        charactersRecyclerView.adapter = characterAdapter

        updateCharactersList()

        val welcomeMsg = findViewById<TextView>(R.id.welcomeMsg)
        welcomeMsg.text = if (characterAdapter.itemCount == 0) {
            "Bienvenido $userName, todavía no tienes personajes, comienza creando uno"
        } else {
            "Bienvenido $userName, selecciona uno de tus personajes"
        }
    }

    override fun onResume() {
        super.onResume()
        println("ENtro en el onResume")
        updateCharactersList()
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

    private fun updateCharactersList() {
        val dbHelper = DatabaseHelper(this)
        val characters = dbHelper.getPersonajesByUserId(userId)
        characterAdapter.updateData(characters)

        val welcomeMsg = findViewById<TextView>(R.id.welcomeMsg)
        val userName = intent.getStringExtra("user_name") ?: "Usuario"
        welcomeMsg.text = if (characters.isEmpty()) {
            "Bienvenido $userName, todavía no tienes personajes, comienza creando uno"
        } else {
            "Bienvenido $userName, selecciona uno de tus personajes"
        }
    }


    private fun showCharacterDialog(
        character: Personaje, context: Context, userId: Int
    ) {
        val dbHelper = DatabaseHelper(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.show_character_dialog, null)


        val listView: ListView = dialogView.findViewById(R.id.listView)
        var characterImage = dialogView.findViewById<ImageView>(R.id.infoImageView)
        val characterName = dialogView.findViewById<TextView>(R.id.charNameText)
        val charInfo = dialogView.findViewById<TextView>(R.id.textView17)
        val attributesText = formatAttributes(character.attributes)
        val charInfoStatsText: TextView = dialogView.findViewById(R.id.charInfoStatsText)
        charInfoStatsText.text = attributesText

        val imageUriString = character.imageUri

        if (imageUriString.isNullOrEmpty()) {
            val className = character.characterClass?.className!!.toLowerCase()
            val resourceName = className ?: "Default"
            val resourceId =
                context.resources.getIdentifier(resourceName, "drawable", context.packageName)

            if (resourceId != 0) {
                characterImage.setImageResource(resourceId)
            } else {
                characterImage.setImageResource(R.drawable.default_avatar)
            }
        } else {
            val imageUri = Uri.parse(imageUriString)
            characterImage.setImageURI(imageUri)
        }

        characterName.text = character.name
        charInfo.setText("${character.characterClass?.className} de nivel ${character.numLevel} ")
        val competenciesFormatted =
            character.competiences.joinToString().replace("[", "").replace("]", "")
                .replace("\"", "")
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
            showSecondDialog(character, userId)
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
                        updateCharactersList()
                    } else {
                        Toast.makeText(
                            context, "Error al eliminar personaje", Toast.LENGTH_SHORT
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

    private fun showSecondDialog(
        character: Personaje, userId: Int
    ) {
        val secondDialogView =
            LayoutInflater.from(this).inflate(R.layout.second_show_character_dialog, null)
        val listView: ListView = secondDialogView.findViewById(R.id.bg_list)
        val background = character.background
        val detailsList = mutableListOf<String>()
        val nextDialogBtn2: Button = secondDialogView.findViewById(R.id.nextDialogBtn2)
        val previousDialogBtn2: Button = secondDialogView.findViewById(R.id.previousDialogBtn2)
        val appearanceTextView = secondDialogView.findViewById<TextView>(R.id.appearanceTextView)
        val textView21 = secondDialogView.findViewById<TextView>(R.id.textView21)


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

        val adapter = BackgroundAdapter(this, detailsList)
        listView.adapter = adapter

        val secondDialogBuilder = AlertDialog.Builder(this)
        secondDialogBuilder.setView(secondDialogView)
        val secondDialog = secondDialogBuilder.create()


        textView21.text = character.history
        appearanceTextView.text = character.appearance

        nextDialogBtn2.setOnClickListener {
            secondDialog.dismiss()
            showThirdDialog(character, this, userId)
        }

        previousDialogBtn2.setOnClickListener {
            secondDialog.dismiss()
            showCharacterDialog(character, this, userId)
        }

        secondDialog.show()
    }

    private fun showThirdDialog(
        character: Personaje, context: Context, userId: Int
    ) {
        val thirdDialogView =
            LayoutInflater.from(context).inflate(R.layout.third_show_character_dialog, null)
        val notesTextView: TextView = thirdDialogView.findViewById(R.id.notesDialogTextView)
        val previousDialogBtn3: Button = thirdDialogView.findViewById(R.id.previousDialogBtn3)
        val textViewJavi = thirdDialogView.findViewById<TextView>(R.id.textViewJavi)
        textViewJavi.text = character.notes ?: "No hay notas disponibles."

        val thirdDialogBuilder = AlertDialog.Builder(context)
        thirdDialogBuilder.setView(thirdDialogView)
        val thirdDialog = thirdDialogBuilder.create()

        previousDialogBtn3.setOnClickListener {
            thirdDialog.dismiss()
            showSecondDialog(character, userId)
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
}