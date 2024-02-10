package com.abriljavier.milistadeheroes

import CharacterAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private lateinit var toolbar: Toolbar

class PersonalPageActivity : AppCompatActivity() {

    private lateinit var charactersRecyclerView: RecyclerView
    private lateinit var characterAdapter: CharacterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val welcomeMsg = findViewById<TextView>(R.id.welcomeMsg)

        val userName = intent.getStringExtra("user_name")

        charactersRecyclerView = findViewById(R.id.recycleView)
        charactersRecyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = DatabaseHelper(this)
        val userId = intent.getIntExtra("user_id", -1)
        println(userId)
        val characters = dbHelper.getPersonajesByUserId(userId)
        println(characters)
        if (characters.isEmpty()){
            welcomeMsg.text = "Bienvenido $userName, todavía no tienes personajes, comienza creando uno"
        } else {
            welcomeMsg.text = "Bienvenido $userName, selecciona uno de tus personajes"
            characterAdapter = CharacterAdapter(characters)
            charactersRecyclerView.adapter = characterAdapter
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