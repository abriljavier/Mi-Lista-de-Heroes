package com.abriljavier.milistadeheroes

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LevelUpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_character_creation_tenth)

        val featuresContainer = findViewById<LinearLayout>(R.id.featuresContainer)
        val nextBtn = findViewById<Button>(R.id.forwardBtn445)

        val charId = intent.getIntExtra("characterId", 0)
        val newLevel = intent.getIntExtra("newLevel", 0)
        val charClass = intent.getIntExtra("charClass", 0)
        val dbHelper = DatabaseHelper(this)
        val characterFeatures: Map<String, String> = dbHelper.getFeaturesByClassIdAndLevel(charClass, newLevel)
        for ((featureName, description) in characterFeatures) {
            if (featureName == "hitDie") continue

            val featureTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val featureText = "$featureName: $description"
                val spannableString = SpannableString(featureText)
                val colonIndex = featureText.indexOf(':')
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    colonIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                text = spannableString
            }

            val density = resources.displayMetrics.density
            val dpValue = 8
            val paddingPixel = (dpValue * density).toInt()

            featuresContainer.addView(featureTextView)
            featuresContainer.setPadding(
                featuresContainer.paddingLeft,
                featuresContainer.paddingTop,
                featuresContainer.paddingRight,
                paddingPixel
            )
        }

        nextBtn.setOnClickListener{
            val hitDieValue = characterFeatures["hitDie"] ?: "1d6"

            val intent = Intent(this, SecondLevelUpActivity::class.java).apply {
                putExtra("hitDie", hitDieValue)
                putExtra("characterId", charId)
            }

            startActivityForResult(intent, 11)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}