package com.google.ar.sceneform.samples.gltf

import android.graphics.Point
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.ScaleGestureDetector
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class FeaturesActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var currentModelNode: TransformableNode? = null
    private var anchorNode: AnchorNode? = null

    // Using WeakReference to avoid memory leaks
    private val modelMap = mutableMapOf<String, WeakReference<ModelRenderable>>()
    private var modelView: ViewRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_features)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        // List of models to preload
        val modelUris = listOf(
            "models/Automatic Levelling.glb",
            "models/Manual Operation.glb",
            "models/Angle Selection.glb",
            "models/RO.glb"
        )

        // Load all models asynchronously
        lifecycleScope.launch {
            preloadModels(modelUris)
        }

        // Load ViewRenderable (UI elements)
        lifecycleScope.launch {
            loadViewRenderable()
        }

        val featureButton1: Button = findViewById(R.id.feature_button_1)
        val featureButton2: Button = findViewById(R.id.feature_button_2)
        val featureButton3: Button = findViewById(R.id.feature_button_3)
        val featureButton4: Button = findViewById(R.id.feature_button_4)

        featureButton1.setOnClickListener {
            val animations = listOf("Colorful glowing arrow.08Action", "Laser cameraAction", "PlaneAction")
            placeModelOnButtonClick("models/Automatic Levelling.glb", animations, R.raw.fall)
        }

        featureButton2.setOnClickListener {
            val animations = listOf("Arrow 1Action.001", "PlaneAction.001")
            placeModelOnButtonClick("models/Manual Operation.glb", animations, R.raw.battery_open)
        }

        featureButton3.setOnClickListener {
            val animations = listOf("Arrow 1Action.003", "Laser cameraAction.002", "Plane.001Action")
            placeModelOnButtonClick("models/Angle Selection.glb", animations, R.raw.fall)
        }

        featureButton4.setOnClickListener {
            val animations = listOf("Arrow 1Action.002", "Laser cameraAction.001", "PlaneAction", "Plane.001Action")
            placeModelOnButtonClick("models/RO.glb", animations, R.raw.fall)
        }
    }
    private suspend fun preloadModels(modelUris: List<String>) = coroutineScope {
        modelUris.map { uri ->
            async {
                loadModel(uri)
            }
        }.awaitAll() // Wait for all models to load
    }


    private suspend fun loadModel(uri: String) {
        if (modelMap.containsKey(uri) && modelMap[uri]?.get() != null) {
            Log.d("FeaturesActivity", "Model already loaded: $uri")
            return
        }

        try {
            Log.d("FeaturesActivity", "Loading model: $uri")
            val modelRenderable = ModelRenderable.builder()
                .setSource(this@FeaturesActivity, Uri.parse(uri))
                .setIsFilamentGltf(true)
                .setRegistryId(uri)
                .await()

            modelMap[uri] = WeakReference(modelRenderable)
            Log.d("FeaturesActivity", "Model loaded successfully: $uri")
        } catch (e: Exception) {
            Log.e("FeaturesActivity", "Error loading model $uri", e)
        }
    }



    private suspend fun loadViewRenderable() {
        try {
            modelView = ViewRenderable.builder()
                .setView(this, R.layout.auto_levelling)
                .await()
        } catch (e: Exception) {
            Log.e("FeaturesActivity", "Error loading ViewRenderable", e)
        }
    }

    private fun placeModelOnButtonClick(modelUri: String, animations: List<String>, soundResId: Int) {
        lifecycleScope.launch {
            while (modelMap[modelUri]?.get() == null) {
                Log.e("FeaturesActivity", "Waiting for model to load: $modelUri")
                delay(500)
            }

            val modelRenderable = modelMap[modelUri]?.get()
            if (modelRenderable == null) {
                Log.e("FeaturesActivity", "Model not loaded properly: $modelUri")
                return@launch
            }

            arFragment.arSceneView.arFrame?.let { frame ->
                if (frame.camera.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                    frame.hitTest(getScreenCenter().x.toFloat(), getScreenCenter().y.toFloat())
                        .firstOrNull { hit ->
                            hit.trackable is Plane && (hit.trackable as Plane).isPoseInPolygon(hit.hitPose)
                        }?.let { hitResult ->
                            removePreviousModel()
                            placeModel(hitResult.createAnchor(), modelRenderable)  // ✅ Pass ModelRenderable
                            playAnimationsInSequence(animations, soundResId)
                        } ?: Log.e("FeaturesActivity", "No valid plane found.")
                } else {
                    Log.e("FeaturesActivity", "AR Tracking not ready.")
                }
            } ?: Log.e("FeaturesActivity", "No AR Frame detected.")
        }
    }


    // ✅ Add this function to remove previous model and free memory
    private fun removePreviousModel() {
        anchorNode?.let {
            arFragment.arSceneView.scene.removeChild(it)
            it.anchor?.detach()
            it.setParent(null)
            anchorNode = null
        }
        currentModelNode = null
        System.gc() // Force garbage collection to free memory
    }


    private fun placeModel(anchor: Anchor, modelRenderable: Any) {
        if (modelRenderable !is Renderable) {
            Log.e("FeaturesActivity", "Invalid model type: $modelRenderable")
            return
        }

        anchorNode?.let {
            arFragment.arSceneView.scene.removeChild(it)
            it.anchor?.detach()
        }

        anchorNode = AnchorNode(anchor).apply {
            setParent(arFragment.arSceneView.scene)
        }

        currentModelNode = TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenderable
            setParent(anchorNode)
            select()
        }

        setupGestureDetectors()
        attachViewRenderable()
    }
    private fun playAnimationsInSequence(animations: List<String>, soundResId: Int) {
        currentModelNode?.renderableInstance?.let { instance ->
            val mediaPlayer = MediaPlayer.create(this, soundResId)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { it.release() }

            lifecycleScope.launch {
                for (animationName in animations) {
                    delay(2000)
                    playAnimation(animationName)
                }
            }
        }
    }
    private fun playAnimation(animationName: String) {
        currentModelNode?.renderableInstance?.animate(animationName)?.start()
    }

    private fun attachViewRenderable() {
        modelView?.let {
            Node().apply {
                setParent(currentModelNode)
                localPosition = Vector3(0f, currentModelNode?.localScale?.y ?: 0.5f, 0f)
                renderable = it
            }
        }
    }

    private fun setupGestureDetectors() {
//        gestureDetector = GestureDetector(this, GestureListener())
//        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        arFragment.arSceneView.scene.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun getScreenCenter(): Point {
        val vw = arFragment.requireView().width
        val vh = arFragment.requireView().height
        return Point(vw / 2, vh / 2)
    }
}
