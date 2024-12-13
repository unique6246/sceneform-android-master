package com.google.ar.sceneform.samples.gltf

import android.graphics.Point
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
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
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FeaturesActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var currentModelNode: TransformableNode? = null
    private var anchorNode: AnchorNode? = null

    private val modelMap = mutableMapOf<String, ModelRenderable>()
    private var modelView: ViewRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_features)

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        val featureButton1: Button = findViewById(R.id.feature_button_1)
        val featureButton2: Button = findViewById(R.id.feature_button_2)

        // Load models
        lifecycleScope.launchWhenCreated {
            loadModel("models/Automatic_Levelling.glb")
            loadModel("models/Manual_Operation.glb")
            loadViewRenderable()
        }

        featureButton1.setOnClickListener {
            val animations = listOf("Automatic Levelling 01", "Automatic Levelling 03", "Automatic Levelling 02")

            placeModelOnButtonClick("models/Automatic_Levelling.glb", animations, R.raw.fall)
        }

        featureButton2.setOnClickListener {
            val animations = listOf("Manual Operation 03 and 04 Intermediate", "Manual Operation 02", "Manual Operation 03",
                "Manual Operation 04","Manual Operation 01","Manual Operation Always")
            placeModelOnButtonClick("models/Manual_Operation.glb", animations, R.raw.battery_open)
        }
    }

    private suspend fun loadModel(uri: String) {
        try {
            val modelRenderable = ModelRenderable.builder()
                .setSource(this, Uri.parse(uri))
                .setIsFilamentGltf(true)
                .await()
            modelMap[uri] = modelRenderable
        } catch (e: Exception) {
            Log.e("FeaturesActivity", "Error loading model $uri", e)
        }
    }

    private suspend fun loadViewRenderable() {
        try {
            modelView = ViewRenderable.builder()
                .setView(this, R.layout.view_renderable_infos)
                .await()
        } catch (e: Exception) {
            Log.e("FeaturesActivity", "Error loading ViewRenderable", e)
        }
    }

    private fun placeModelOnButtonClick(modelUri: String, animations: List<String>, soundResId: Int) {
        arFragment.arSceneView.arFrame?.let { frame ->
            if (frame.camera.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                frame.hitTest(getScreenCenter().x.toFloat(), getScreenCenter().y.toFloat())
                    .firstOrNull { hit ->
                        hit.trackable is Plane && (hit.trackable as Plane).isPoseInPolygon(hit.hitPose)
                    }?.let { hitResult ->
                        placeModel(hitResult.createAnchor(), modelUri)
                        playAnimationsInSequence(animations, soundResId)
                    }
            }
        }
    }

    private fun placeModel(anchor: Anchor, modelUri: String) {
        val modelRenderable = modelMap[modelUri]
        if (modelRenderable == null) {
            Log.e("FeaturesActivity", "Model not loaded: $modelUri")
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
        } ?: Log.e("FeaturesActivity", "Model instance is not initialized")
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
        } ?: Log.e("FeaturesActivity", "ViewRenderable is not initialized")
    }

    private fun setupGestureDetectors() {
        gestureDetector = GestureDetector(this, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        arFragment.arSceneView.scene.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }



    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            currentModelNode?.apply {
                localPosition = Vector3(
                    localPosition.x - distanceX * 0.001f,
                    localPosition.y + distanceY * 0.001f,
                    localPosition.z
                )
            }
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            currentModelNode?.let {
                it.localScale = it.localScale.scaled(detector.scaleFactor)
            }
            return true
        }
    }

    private fun getScreenCenter(): Point {
        val vw = arFragment.requireView().width
        val vh = arFragment.requireView().height
        return Point(vw / 2, vh / 2)
    }
}
