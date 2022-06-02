package com.example.fic_o_fit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPushup = findViewById<Button>(R.id.btnPushup)
        val btnSitup = findViewById<Button>(R.id.btnSitup)
        val btnRun = findViewById<Button>(R.id.btnRun)

        btnPushup.setOnClickListener {
            val intent = Intent(this, PushupActivity::class.java)
            startActivity(intent)
        }

        btnSitup.setOnClickListener {
            val intent = Intent(this, SitupActivity::class.java)
            startActivity(intent)
        }

//        btnRun.setOnClickListener {
//            val intent = Intent(this, RunnerActivity::class.java)
//            startActivity(intent)
//        }

    }
}