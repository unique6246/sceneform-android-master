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

class ModelUseCasesActivity6 : AppCompatActivity() {

    private lateinit var onboardingOverlay: View
    private lateinit var tutorialText: TextView
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private var tutorialStep = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        val settingsButton = findViewById<Button>(R.id.settings_button)
        val featuresButton = findViewById<ImageButton>(R.id.features)
        val useCasesButton = findViewById<ImageButton>(R.id.use_cases)

        // Onboarding elements
        onboardingOverlay = findViewById(R.id.onboarding_overlay)
        tutorialText = findViewById(R.id.tutorial_text)
        nextButton = findViewById(R.id.next_button)
        skipButton = findViewById(R.id.skip_button)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch_ModelUseCases", true)

        if (!isFirstLaunch) {
            onboardingOverlay.visibility = View.GONE // Hide overlay if already seen
        } else {
            showTutorialStep()
        }

        // Tutorial navigation
        nextButton.setOnClickListener {
            tutorialStep++
            showTutorialStep()
        }

        // Skip tutorial
        skipButton.setOnClickListener {
            onboardingOverlay.visibility = View.GONE
            saveFirstLaunchPreference()
        }

        // Button listeners
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        featuresButton.setOnClickListener {
            val intent = Intent(this, FeaturesActivity::class.java)
            startActivity(intent)
        }

        useCasesButton.setOnClickListener {
            val intent = Intent(this, UseCasesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showTutorialStep() {
        when (tutorialStep) {
            0 -> {
                tutorialText.text = "Tap here to explore various features!"
                findViewById<View>(R.id.highlight_features_button).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_use_cases_button).visibility = View.GONE
            }
            1 -> {
                tutorialText.text = "Tap here to view the use cases of the tool!"
                findViewById<View>(R.id.highlight_features_button).visibility = View.GONE
                findViewById<View>(R.id.highlight_use_cases_button).visibility = View.VISIBLE
            }
            2 -> {
                onboardingOverlay.visibility = View.GONE
                saveFirstLaunchPreference()
            }
        }
    }

    private fun saveFirstLaunchPreference() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch_ModelUseCases", false) // Mark tutorial as completed
        editor.apply()
    }
}
