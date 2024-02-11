package com.abriljavier.milistadeheroes

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LevelUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_character_creation_tenth)

        val featuresContainer = findViewById<LinearLayout>(R.id.featuresContainer)

        val charId = intent.getIntExtra("characterId", 0)
        val newLevel = intent.getIntExtra("newLevel", 1)
        val charClass = intent.getIntExtra("charClass", 0)
        val dbHelper = DatabaseHelper(this)
        val characterFeatures = dbHelper.getFeaturesByClassIdAndLevel(charClass, newLevel)

        for ((featureName, description) in characterFeatures) {
            if (featureName == "hitDie") continue

            val featureTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { params ->
                    val marginsInDp = 20
                    val density = resources.displayMetrics.density
                    val marginInPixels = (marginsInDp * density).toInt()
                    params.setMargins(marginInPixels, 0, marginInPixels, marginInPixels)
                }

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

            featuresContainer.addView(featureTextView)
        }

        val layoutParams = featuresContainer.layoutParams as ViewGroup.MarginLayoutParams
        val marginTopDp = 20
        val density = resources.displayMetrics.density
        val marginTopPx = (marginTopDp * density).toInt()
        layoutParams.topMargin = marginTopPx
        featuresContainer.layoutParams = layoutParams

        val nextBtn = findViewById<Button>(R.id.forwardBtn445)
        nextBtn.setOnClickListener {
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