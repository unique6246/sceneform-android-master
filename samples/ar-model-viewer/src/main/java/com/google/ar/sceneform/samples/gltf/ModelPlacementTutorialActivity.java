package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ModelPlacementTutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private int tutorialStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_placement_tutorial);

        tutorialText = findViewById(R.id.tutorialText);
        Button nextButton = findViewById(R.id.nextButton);
        Button skipButton = findViewById(R.id.skipButton);

        nextButton.setOnClickListener(v -> showNextStep());
        skipButton.setOnClickListener(v -> finishTutorial());

        showNextStep();
    }

    private void showNextStep() {
        switch (tutorialStep) {
            case 0:
                tutorialText.setText("Step 1: Move your device to detect a plane.");
                break;

            case 1:
                tutorialText.setText("Step 2: Tap the button to list features.");
                break;

            case 2:
                tutorialText.setText("Step 3: Select a model to place.");
                break;

            case 3:
                finishTutorial();
                return;
        }
        tutorialStep++;
    }

    private void finishTutorial() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("TutorialCompleted", true).apply();
        startActivity(new Intent(this, ModelPlacementActivity5.class));
        finish();
    }
}
