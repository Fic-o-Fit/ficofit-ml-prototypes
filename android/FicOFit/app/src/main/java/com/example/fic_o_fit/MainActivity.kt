package com.example.fic_o_fit

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCal = findViewById(R.id.btnCal) as Button
        btnCal.setOnClickListener {
            val intent = Intent(this, PushupActivity::class.java)
            startActivity(intent)
            println("testestes")
            Log.d("TEST", "testestes")
        }

    }
}