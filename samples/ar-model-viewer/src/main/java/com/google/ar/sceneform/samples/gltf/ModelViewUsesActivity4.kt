package com.google.ar.sceneform.samples.gltf

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ModelViewUsesActivity4 : AppCompatActivity() {

    private lateinit var onboardingOverlay: View
    private lateinit var tutorialText: TextView
    private lateinit var skipButton: Button
    private var tutorialStep = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val placeButton = findViewById<ImageButton>(R.id.place_button)
        val homeButton = findViewById<Button>(R.id.home_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        // Onboarding elements
        onboardingOverlay = findViewById(R.id.onboarding_overlay)
        tutorialText = findViewById(R.id.tutorial_text)
        skipButton = findViewById(R.id.skip_button)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch_ModelView", true)

        if (!isFirstLaunch) {
            onboardingOverlay.visibility = View.GONE // Hide overlay if already seen
        } else {
            showTutorialStep()
        }


        // Skip tutorial
        skipButton.setOnClickListener {
            onboardingOverlay.visibility = View.GONE
            saveFirstLaunchPreference()
        }

        // Button listeners
        placeButton.setOnClickListener {
            val intent = Intent(this, ModelPlacementActivity5::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, ScanningActivity3::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showTutorialStep() {
        when (tutorialStep) {
            0 -> {
                tutorialText.text = "Tap here to place a 3D model in your environment!"
                findViewById<View>(R.id.highlight_place_button).visibility = View.VISIBLE
//                findViewById<View>(R.id.highlight_model_button).visibility = View.GONE
            }

            1 -> {
                onboardingOverlay.visibility = View.GONE
                saveFirstLaunchPreference()
            }
        }
    }

    private fun saveFirstLaunchPreference() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch_ModelView", false) // Mark tutorial as completed
        editor.apply()
    }
}
