package com.google.ar.sceneform.samples.gltf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HomeActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding) // Include onboarding layout

        val second = findViewById<ImageButton>(R.id.button)
        val onboardingOverlay = findViewById<View>(R.id.onboarding_overlay)
        val skipButton = findViewById<Button>(R.id.skip_button)

        // Tutorial action
        second.setOnClickListener {
            onboardingOverlay.visibility = View.GONE // Hide overlay when understood
            val intent = Intent(this, ScanToolActivity2::class.java)
            startActivity(intent)
        }

        // Skip tutorial
        skipButton.setOnClickListener {
            onboardingOverlay.visibility = View.GONE
        }
    }
}
