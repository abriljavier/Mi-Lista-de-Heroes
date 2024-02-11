package com.abriljavier.milistadeheroes

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.abriljavier.milistadeheroes.dataclasses.Users
import java.sql.Types.NULL

private lateinit var toolbar: Toolbar

class CreateUserActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createuser)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val createAccountBtn = findViewById<Button>(R.id.createAccountBtn)
        val dbHelper = DatabaseHelper(this)

        createAccountBtn.setOnClickListener{
            var usernametext = inputUsername.text.toString()
            var passwordText = inputPassword.text.toString()
            if (usernametext.length == 0 || usernametext == " "){
                showAlertDialog("Error", "Por favor ingresa un nombre de usuario válido.", this@CreateUserActivity)
            } else if (passwordText.length == 0 || passwordText == " "){
                showAlertDialog("Error", "Por favor ingresa una contraseña válida.", this@CreateUserActivity)
            } else {
                val existingUser = dbHelper.getUserByUsername(usernametext)
                if (existingUser != null) {
                    showAlertDialog("Error", "Ya existe un usuario con ese nombre, prueba otro :D", this@CreateUserActivity)
                } else {
                    val user = Users(user_id = NULL, username = usernametext, password = passwordText, pc_id = null)
                    dbHelper.insertUser(user)
                    Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

private fun showAlertDialog(title: String, message: String, context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("Aceptar", null)
    val dialog = builder.create()
    dialog.show()
}