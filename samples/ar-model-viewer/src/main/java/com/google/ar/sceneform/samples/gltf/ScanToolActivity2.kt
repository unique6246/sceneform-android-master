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

class ScanToolActivity2 : AppCompatActivity() {

    private lateinit var onboardingOverlay: View
    private lateinit var tutorialText: TextView
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private var tutorialStep = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val scanButton = findViewById<ImageButton>(R.id.scan_button)
        val toolsButton = findViewById<ImageButton>(R.id.tools_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        // Onboarding elements
        onboardingOverlay = findViewById(R.id.onboarding_overlay)
        tutorialText = findViewById(R.id.tutorial_text)
        nextButton = findViewById(R.id.next_button)
        skipButton = findViewById(R.id.skip_button)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch_ScanTool", true)

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
        scanButton.setOnClickListener {
            val intent = Intent(this, ScanningActivity3::class.java)
            startActivity(intent)
        }

        toolsButton.setOnClickListener {
            val intent = Intent(this, ToolsListActivity7::class.java)
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
                tutorialText.text = "Tap here to scan and place models in your environment!"
                findViewById<View>(R.id.highlight_scan_button).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_tools_button).visibility = View.GONE
                findViewById<View>(R.id.highlight_settings_button).visibility = View.GONE
            }
            1 -> {
                tutorialText.text = "Tap here to explore available tools and use cases!"
                findViewById<View>(R.id.highlight_scan_button).visibility = View.GONE
                findViewById<View>(R.id.highlight_tools_button).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_settings_button).visibility = View.GONE
            }
            2 -> {
                tutorialText.text = "Tap here to open settings and customize your experience!"
                findViewById<View>(R.id.highlight_scan_button).visibility = View.GONE
                findViewById<View>(R.id.highlight_tools_button).visibility = View.GONE
                findViewById<View>(R.id.highlight_settings_button).visibility = View.VISIBLE
            }
            3 -> {
                onboardingOverlay.visibility = View.GONE
                saveFirstLaunchPreference()
            }
        }
    }

    private fun saveFirstLaunchPreference() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch_ScanTool", false) // Mark tutorial as completed
        editor.apply()
    }
}
