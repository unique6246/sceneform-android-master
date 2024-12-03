package com.google.ar.sceneform.samples.gltf

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class ModelUseCasesActivity6 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        val sixth=findViewById<Button>(R.id.home_button)
        sixth.setOnClickListener{
            val intent2= Intent(this, ModelViewUsesActivity4::class.java)
            startActivity(intent2)
        }
        val fourth=findViewById<Button>(R.id.settings_button)
        fourth.setOnClickListener {
            val intent=Intent(this, SettingsActivity::class.java)
            startActivity(intent)

        }
        val features=findViewById<ImageButton>(R.id.features)
        features.setOnClickListener{
            val intent =Intent(this,FeaturesActivity::class.java)
            startActivity(intent)
        }

        val useCases=findViewById<ImageButton>(R.id.use_cases)
        useCases.setOnClickListener{
            val intent =Intent(this,UseCasesActivity::class.java)
            startActivity(intent)
        }
    }
}