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
    private ImageView tutorialImage;
    private Button nextButton;
    private int tutorialStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_placement_tutorial);

        tutorialText = findViewById(R.id.tutorialText);
        tutorialImage = findViewById(R.id.tutorialImage);
        nextButton = findViewById(R.id.nextButton);
        Button skipButton = findViewById(R.id.skipButton);

        nextButton.setOnClickListener(v -> showNextStep());
        skipButton.setOnClickListener(v -> finishTutorial());

        showNextStep();
    }

    private void showNextStep() {
        switch (tutorialStep) {
            case 0:
                tutorialText.setText("Step 1: Detect the AR plane to place the 3D model.");
                Glide.with(this).load(R.drawable.gif1).into(tutorialImage);
                break;
            case 1:
                tutorialText.setText("Step 2: Use the preferred environment to place the model.");
                Glide.with(this).load(R.drawable.img_1).into(tutorialImage);
                break;
            case 2:
                tutorialText.setText("Step 3: Press the 'Place Model' button to finalize placement.");
                Glide.with(this).load(R.drawable.gif2).into(tutorialImage);
                break;
            case 3:
                tutorialText.setText("You're ready to go!");
//                Glide.with(this).load(R.drawable.tutorial_step_4).into(tutorialImage);
                nextButton.setText("Finish");
                break;
            case 4:
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
