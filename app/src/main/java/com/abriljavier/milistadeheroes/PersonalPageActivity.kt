package com.abriljavier.milistadeheroes

import CharacterAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abriljavier.milistadeheroes.dataclasses.Personaje

private lateinit var toolbar: Toolbar

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
        if (characters.isEmpty()) {
            welcomeMsg.text =
                "Bienvenido $userName, todavía no tienes personajes, comienza creando uno"
        } else {
            welcomeMsg.text = "Bienvenido $userName, selecciona uno de tus personajes"
            characterAdapter = CharacterAdapter(characters) { character ->
                showCharacterDialog(character)
            }
            charactersRecyclerView.adapter = characterAdapter
            updateCharactersList()
        }


    }

    override fun onResume() {
        super.onResume()
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
        characterAdapter.notifyDataSetChanged()
    }

    private fun showCharacterDialog(character: Personaje) {
        val dbHelper = DatabaseHelper(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.show_character_dialog, null)

        val layoutPageOne = dialogView.findViewById<View>(R.id.layoutPageOne)
//        val layoutPageTwo = dialogView.findViewById<View>(R.id.layoutPageTwo)

        val characterImage = dialogView.findViewById<ImageView>(R.id.infoImageView)
        val characterName = dialogView.findViewById<TextView>(R.id.charNameText)
        val charInfo = dialogView.findViewById<TextView>(R.id.textView17)
        characterName.text = character.name
        charInfo.setText("${character.characterClass?.className} de nivel ${character.numLevel} ")


        val nextPageBtn = dialogView.findViewById<Button>(R.id.nextPageBtn)
        nextPageBtn.setOnClickListener {
            layoutPageOne.visibility = View.GONE
//            layoutPageTwo.visibility = View.VISIBLE

            // Configurar la información para la segunda página en layoutPageTwo...
        }


        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()

        val deleteBtn: Button = dialogView.findViewById(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Eliminar personaje")
                setMessage("¿Estás seguro de querer eliminar este personaje?")
                setPositiveButton("Eliminar") { dialogInterface, i ->
                    if (character.pj_id?.let { it1 -> dbHelper.deleteCharacter(it1) } == true) {
                        Toast.makeText(
                            this@PersonalPageActivity, "Personaje eliminado", Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        updateCharactersList()
                    } else {
                        Toast.makeText(
                            this@PersonalPageActivity,
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
            val newLevel = character.numLevel?.plus(1)
            var classId =
                character.characterClass?.className?.let { it1 -> dbHelper.getClassIdByName(it1) };
            var newLevelThings = newLevel?.let { it1 ->
                dbHelper.updateCharacterLevelAndFeatures(character.characterClass?.classId ?: 0,
                    it1, classId!!)
            }
            character.featuresByLevel.add(newLevelThings.toString())
            character.numLevel = newLevel
            Toast.makeText(this, "Personaje subido a nivel $newLevel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            updateCharactersList()
        }

        dialog.show()
    }

}