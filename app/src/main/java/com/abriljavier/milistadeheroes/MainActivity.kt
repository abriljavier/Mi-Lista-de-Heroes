package com.abriljavier.milistadeheroes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var currentPage: String
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        currentPage = "Home"

        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        inputUsername.setText("")
        inputPassword.setText("")

        val createAccountBtn = findViewById<Button>(R.id.createAccountBtn)
        val logInBtn = findViewById<Button>(R.id.logInBtn)

        val dbHelper = DatabaseHelper(this)
        dbHelper.writableDatabase

        createAccountBtn.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        }
        logInBtn.setOnClickListener {
            var usernametext = inputUsername.text.toString()
            var passwordText = inputPassword.text.toString()
            if (usernametext.isEmpty() || usernametext == " ") {
                showAlertDialog(
                    "Fallo en la autenticación",
                    "Por favor ingresa un nombre de usuario válido.",
                    this@MainActivity
                )
            } else if (passwordText.isEmpty() || passwordText == " ") {
                showAlertDialog(
                    "Fallo en la autenticación",
                    "Por favor ingresa una contraseña válida.",
                    this@MainActivity
                )
            } else {
                val existingUser = dbHelper.getUserByUsername(usernametext)
                if (existingUser == null) {
                    showAlertDialog(
                        "Fallo en la autenticación",
                        "No existe ningun usuario con ese nombre",
                        this@MainActivity
                    )
                } else {
                    if (passwordText == existingUser?.password) {
                        openPersonalPage(this@MainActivity, existingUser)
                    } else {
                        showAlertDialog(
                            "Fallo en la autenticación",
                            "La contraseña ingresada es incorrecta.",
                            this@MainActivity
                        )
                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        when (currentPage) {
            "Home" -> {
                menuInflater.inflate(R.menu.home_menu, menu)
                val menuItem = menu.findItem(R.id.seeMoreBtn)
                menuItem.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.seeMoreBtn -> {
                            showSeeMoreDialog(this@MainActivity)
                            true
                        }

                        else -> false
                    }
                }
            }
//            Page.PROFILE -> menuInflater.inflate(R.menu.menu_profile, menu)
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        inputUsername.text.clear()
        inputPassword.text.clear()
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

private fun showSeeMoreDialog(context: Context) {
    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.setTitle("Sobre la app")
    dialogBuilder.setMessage("Aplicación creada por Javier Abril.\n\nEsta aplicación no está monetizada y no guardo los derechos de los datos aqui representados, todos los derechos pertenecen a Wizard of the Coast.\n\nhttps://github.com/abriljavier")
    dialogBuilder.setPositiveButton("Aceptar") { dialog, which ->
        dialog.dismiss()
    }
    val dialog = dialogBuilder.create()
    dialog.setCancelable(false)
    dialog.show()
}

private fun openPersonalPage(context: Context, user: Users) {
    val intent = Intent(context, PersonalPageActivity::class.java)
    intent.putExtra("user_name", user.username)
    intent.putExtra("user_password", user.password)
    intent.putExtra("user_data", user.pc_id)
    context.startActivity(intent)
}



