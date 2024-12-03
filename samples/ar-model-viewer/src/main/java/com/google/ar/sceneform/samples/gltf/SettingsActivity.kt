package com.google.ar.sceneform.samples.gltf

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
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
