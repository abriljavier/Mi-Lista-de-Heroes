package com.abriljavier.milistadeheroes

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

private lateinit var toolbar: Toolbar
private lateinit var userEdit: EditText
private lateinit var passwordEdit: EditText
private lateinit var changePassBtn: Button
private lateinit var welcomeMsg: TextView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name")
        val userPass = intent.getStringExtra("user_pass")
        val pcId = intent.getIntExtra("user_pc_id", -1)

        welcomeMsg = findViewById(R.id.welcomeMsg)
        welcomeMsg.text =
            "Bienvenido $userName, aqui puedes ver tu perfil y modificar la contraseña."
        userEdit = findViewById(R.id.usernameEdit)
        userEdit.setText("$userName")
        userEdit.isFocusable = false
        userEdit.isClickable = false
        userEdit.isCursorVisible = false
        passwordEdit = findViewById(R.id.passwordEdit)
        passwordEdit.setText("$userPass")
        changePassBtn = findViewById(R.id.changePassBtn)
        changePassBtn.setOnClickListener {
            val newPassword = passwordEdit.text.toString()
            val confirmDialog = AlertDialog.Builder(this)
            confirmDialog.setTitle("Confirmar")
            confirmDialog.setMessage("¿Estás seguro de que deseas cambiar tu contraseña?")
            confirmDialog.setPositiveButton("Sí") { _, _ ->
                val dbHelper = DatabaseHelper(this)
                val success = dbHelper.updateUserPassword(userId, newPassword)
                dbHelper.close()
                println(success)
                if (success) {
                    Toast.makeText(this, "Contraseña modificada correctamente", Toast.LENGTH_SHORT)
                        .show()
                    passwordEdit.setText(newPassword)
                } else {
                    Toast.makeText(this, "Error al modificar la contraseña", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            confirmDialog.setNegativeButton("Cancelar") { _, _ ->
            }

            confirmDialog.show()
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