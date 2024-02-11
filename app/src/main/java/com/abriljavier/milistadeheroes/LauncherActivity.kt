package com.abriljavier.milistadeheroes

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val loadingCircle = findViewById<ImageView>(R.id.loading_circle)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        loadingCircle.startAnimation(rotateAnimation)

        mediaPlayer = MediaPlayer.create(this, R.raw.main).apply {
            isLooping = true
            setVolume(1.0f, 1.0f)
            start()
        }

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}