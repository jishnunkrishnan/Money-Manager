package com.jishnunkrishnan.moneymanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        this.handler = Handler()
        handler.postDelayed({

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}