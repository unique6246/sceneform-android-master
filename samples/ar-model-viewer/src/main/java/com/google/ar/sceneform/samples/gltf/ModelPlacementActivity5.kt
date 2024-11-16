package com.google.ar.sceneform.samples.gltf

import android.graphics.Point
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
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await

class ModelPlacementActivity5 : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var modelNode: TransformableNode? = null
    private var anchorNode: AnchorNode? = null

    private var modelRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2) // Set the layout for the activity

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        val placeModelButton: Button = findViewById(R.id.place_model_button)
        placeModelButton.setOnClickListener {
            arFragment.arSceneView.arFrame?.let { frame ->
                if (frame.camera.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                    frame.hitTest(getScreenCenter().x.toFloat(), getScreenCenter().y.toFloat())
                        .firstOrNull { hit ->
                            hit.trackable is Plane && (hit.trackable as Plane).isPoseInPolygon(hit.hitPose)
                        }?.let { hitResult ->
                            placeModel(hitResult.createAnchor())
                        }
                }
            }
        }

        setupGestureDetectors()

        // Load the model asynchronously
        lifecycleScope.launchWhenCreated {
            loadModel()
        }
    }

    // Load the 3D model asynchronously
    private suspend fun loadModel() {
        try {
            modelRenderable = ModelRenderable.builder()
                .setSource(this, Uri.parse("models/Bosch1.glb")) // Load from assets
                .setIsFilamentGltf(true)
                .await() // Await the loading of the model
        } catch (e: Exception) {
            Log.e("ModelPlacementActivity5", "Error loading model", e)
        }
    }

    // Place the model at the anchor point
    private fun placeModel(anchor: Anchor) {
        modelRenderable?.let { modelRenderable ->
            anchorNode?.let {
                arFragment.arSceneView.scene.removeChild(it)
                it.anchor?.detach()
            }

            // Create the AnchorNode with the given anchor
            anchorNode = AnchorNode(anchor).apply {
                setParent(arFragment.arSceneView.scene)
            }

            // Create the TransformableNode and attach the model
            modelNode = TransformableNode(arFragment.transformationSystem).apply {
                renderable = modelRenderable // Set the model for the node
                localScale=Vector3(0.5f, 0.5f, 0.5f)
                localPosition= Vector3(0.0f, 3.0f, -10.0f)
                setParent(anchorNode) // Attach the node to the anchor node
                select() // Enable the model for transformations
                renderableInstance.setCulling(false)
                renderableInstance.animate(true).start()
            }
        } ?: run {
            Log.e("ModelPlacementActivity5", "Model is not loaded yet")
        }
    }

    // Setup gesture detectors for scaling and scrolling
    private fun setupGestureDetectors() {
        gestureDetector = GestureDetector(this, GestureListener())
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        arFragment.arSceneView.scene.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    // GestureListener to handle scrolling gestures
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private var previousX: Float = 0f
        private var previousY: Float = 0f

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (e1 != null) {
                modelNode?.apply {
                    val dx = e2.rawX - previousX
                    val dy = e2.rawY - previousY
                    localPosition.x += dx * 0.001f
                    localPosition.y -= dy * 0.001f
                    previousX = e2.rawX
                    previousY = e2.rawY
                }
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            e.let {
                previousX = it.rawX
                previousY = it.rawY
            }
            return true
        }
    }

    // ScaleListener to handle scaling gestures
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            modelNode?.let {
                val scaleFactor = detector.scaleFactor
                it.localScale = it.localScale.scaled(scaleFactor)
            }
            return true
        }
    }

    // Get the center point of the screen
    private fun getScreenCenter(): Point {
        val vw = arFragment.requireView().width
        val vh = arFragment.requireView().height
        return Point(vw / 2, vh / 2)
    }
}
