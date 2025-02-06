package com.google.ar.sceneform.samples.gltf

import android.content.Intent
import android.content.SharedPreferences
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

        // SharedPreferences to track first-time launch
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            onboardingOverlay.visibility = View.VISIBLE // Show tutorial if reset
        } else {
            onboardingOverlay.visibility = View.GONE
        }


        // When user clicks tutorial button
        second.setOnClickListener {
            onboardingOverlay.visibility = View.GONE
            val intent = Intent(this, ModelViewUsesActivity4::class.java)
            startActivity(intent)
            saveFirstLaunchPreference()
        }

        // Skip tutorial and store preference
        skipButton.setOnClickListener {
            onboardingOverlay.visibility = View.GONE
            saveFirstLaunchPreference()
        }
    }

    private fun saveFirstLaunchPreference() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch", false) // Set first launch as false
        editor.apply()
    }
}
