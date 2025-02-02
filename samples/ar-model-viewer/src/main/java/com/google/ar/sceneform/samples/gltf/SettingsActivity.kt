package com.google.ar.sceneform.samples.gltf

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var onboardingOverlay: View
    private lateinit var tutorialText: TextView
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private var tutorialStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        // Onboarding elements
        onboardingOverlay = findViewById(R.id.onboarding_overlay)
        tutorialText = findViewById(R.id.tutorial_text)
        nextButton = findViewById(R.id.next_button)
        skipButton = findViewById(R.id.skip_button)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch_Settings", true)

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
        findViewById<View>(R.id.language_section).setOnClickListener {
            onLanguageClick(it)
        }
        findViewById<View>(R.id.contact_us_section).setOnClickListener {
            onContactUsClick(it)
        }
        findViewById<View>(R.id.about_us_section).setOnClickListener {
            onAboutUsClick(it)
        }
    }

    private fun showTutorialStep() {
        when (tutorialStep) {
            0 -> {
                tutorialText.text = "This is where you can see the app version."
                findViewById<View>(R.id.highlight_version_section).visibility = View.VISIBLE
            }
            1 -> {
                tutorialText.text = "Tap here to change your language settings."
                findViewById<View>(R.id.highlight_language_section).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_version_section).visibility = View.GONE
            }
            2 -> {
                tutorialText.text = "Contact us if you need any help!"
                findViewById<View>(R.id.highlight_contact_us_section).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_language_section).visibility = View.GONE
            }
            3 -> {
                tutorialText.text = "Learn more about us."
                findViewById<View>(R.id.highlight_about_us_section).visibility = View.VISIBLE
                findViewById<View>(R.id.highlight_contact_us_section).visibility = View.GONE
            }
            4 -> {
                onboardingOverlay.visibility = View.GONE
                saveFirstLaunchPreference()
            }
        }
    }

    private fun saveFirstLaunchPreference() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstLaunch_Settings", false) // Mark tutorial as completed
        editor.apply()
    }

    // Method for "About Us" click
    fun onAboutUsClick(view: View) {
        val aboutUsUrl = "https://www.bosch.in/our-company/bosch-in-india/#:~:text=In%20India%2C%20Bosch%20is%20a,end%20engineering%20and%20technology%20solutions"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(aboutUsUrl)
        }
        startActivity(intent)
    }

    // Method for "Contact Us" click
    fun onContactUsClick(view: View) {
        val email = "praveenwppe@gmail.com"
        val phone = "9738982170"
        val subject = "Support Request"
        val body = "Please describe your problem here."

        // Launch email intent
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        // Launch dialer intent
        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))

        // Show options
        val chooser = Intent.createChooser(emailIntent, "Contact us via")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(dialIntent))
        startActivity(chooser)
    }

    // Method for "Language" click
    fun onLanguageClick(view: View) {
        Toast.makeText(this, "Language settings coming soon!", Toast.LENGTH_SHORT).show()
        // You can open a language selection activity/dialog here
    }
}
