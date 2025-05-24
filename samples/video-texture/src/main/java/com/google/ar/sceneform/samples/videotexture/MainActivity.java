package com.google.ar.sceneform.samples.videotexture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.VideoNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnTapArPlaneListener {

    private final List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private ArFragment arFragment;
    private Uri selectedVideoUri = null;
    private static final int REQUEST_PERMISSION = 1;

    private AnchorNode currentAnchorNode = null;
    private MediaPlayer currentMediaPlayer = null;

    private ActivityResultLauncher<Intent> videoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            ((ViewGroup.MarginLayoutParams) toolbar.getLayoutParams()).topMargin =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            return WindowInsetsCompat.CONSUMED;
        });

        getSupportFragmentManager().addFragmentOnAttachListener(this);

        if (savedInstanceState == null && Sceneform.isSupported(this)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.arFragment, ArFragment.class, null)
                    .commit();
        }

        // Register video picker launcher
        videoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedVideoUri = data.getData();
                            Toast.makeText(this, "Video selected. Tap on a surface to place it.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Setup video picker button
        Button pickVideoButton = findViewById(R.id.pick_video_button);
        pickVideoButton.setOnClickListener(v -> {
            if (checkPermission()) {
                openVideoPicker();
            } else {
                requestPermission();
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        videoPickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openVideoPicker();
        } else {
            Toast.makeText(this, "Permission denied to access videos.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnTapArPlaneListener(this);
        }
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, android.view.MotionEvent motionEvent) {
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Please select a video first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove existing video if already placed
        if (currentAnchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(currentAnchorNode);
            currentAnchorNode.getAnchor().detach();
            currentAnchorNode.setParent(null);
            currentAnchorNode = null;
        }

        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
        }

        // Create new anchor and node
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        currentAnchorNode = anchorNode;

        TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
        modelNode.setParent(anchorNode);

        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(this, selectedVideoUri);
            player.setLooping(true);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to play video", Toast.LENGTH_SHORT).show();
            return;
        }

        currentMediaPlayer = player;

        VideoNode videoNode = new VideoNode(this, player, null, new VideoNode.Listener() {
            @Override
            public void onCreated(VideoNode videoNode) {}

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Unable to load video node", Toast.LENGTH_LONG).show();
            }
        });

        videoNode.setParent(modelNode);
        modelNode.select();
    }


    @Override
    protected void onStart() {
        super.onStart();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
