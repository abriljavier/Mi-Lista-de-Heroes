package com.abriljavier.milistadeheroes

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondLevelUpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_character_creation_eleventh)

        var newHp = 0
        val hitDieValue = intent.getStringExtra("hitDie")?.split("d")?.last()?.toIntOrNull() ?: 6
        val charId = intent.getIntExtra("characterId", 0)

        val textView14 = findViewById<TextView>(R.id.textView14)
        textView14.visibility = View.INVISIBLE
        val competenciasLayout = findViewById<LinearLayout>(R.id.competenciasLayout)
        competenciasLayout.visibility = View.INVISIBLE
        var editText = findViewById<EditText>(R.id.pgInput)
        editText.hint = hitDieValue.toString()
        var rollBtn = findViewById<Button>(R.id.rollTheDice)
        rollBtn.setOnClickListener{
            val rollResult = (1..hitDieValue).random()
            editText.setText(rollResult.toString())
        }
        val finishBtn = findViewById<Button>(R.id.finishBtn)
        finishBtn.setOnClickListener{
            val dbHelper = DatabaseHelper(this)
            val currentHp = dbHelper.getCharacterHitPoints(charId)
            val rollResult = editText.text.toString().toIntOrNull()
            val additionalHp = rollResult ?: (hitDieValue / 2)
            newHp = if (editText.text != null){
                currentHp+additionalHp
            } else {
                currentHp+ rollResult!!
            }
            dbHelper.updateCharacterLevelAndHitPoints(charId, newHp)

            setResult(Activity.RESULT_OK)
            finish()
        }



    }

}