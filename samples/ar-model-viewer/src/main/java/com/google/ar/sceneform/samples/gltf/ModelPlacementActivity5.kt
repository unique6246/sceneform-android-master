package com.google.ar.sceneform.samples.gltf

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ModelPlacementActivity5 : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private val loadedModels = mutableMapOf<String, ModelRenderable?>()
    private var anchorNode: AnchorNode? = null
    private var modelNode: TransformableNode? = null
    private lateinit var fab: FloatingActionButton
    private lateinit var fabMenu: LinearLayout
    private var fabOpened = false

    private val models = mapOf(
        "Automatic Levelling" to Pair("models/AL.glb", listOf("Colorful glowing arrow.08Action", "Laser cameraAction", "PlaneAction")),
        "Manual Operation" to Pair("models/MO.glb", listOf("Arrow 1Action.001", "PlaneAction.001")),
        "Angle Selection" to Pair("models/AS.glb", listOf("Arrow 1Action.003", "Laser cameraAction.002", "Plane.001Action")),
        "Rotational Operation" to Pair("models/RO.glb", listOf("Arrow 1Action.002", "Laser cameraAction.001", "PlaneAction", "Plane.001Action")),
        "Battery Removal" to Pair("models/BR.glb", listOf("EmptyAction","2","1","Battery  v6Action"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        fab = findViewById(R.id.fab)
        fabMenu = findViewById(R.id.fab_menu)
        lifecycleScope.launch {
            preloadModels()
//            listAllAvailableAnimations()
        }
        enableFabMenuInteraction()
    }

    private fun enableFabMenuInteraction() {
        // Set FAB button click listener
        fab.setOnClickListener {
            if (fabOpened) {
                hideFabMenu()
            } else {
                showFabMenu()
            }
        }
    }

//
    private fun showFabMenu() {
        fabOpened = true
        fabMenu.visibility = View.VISIBLE

        // Clear existing buttons if already shown
        fabMenu.removeAllViews()

        // Set the buttons to be in the vertical layout beside the main FAB
        for (modelName in models.keys) {
            val modelButton = Button(this).apply {
                text = modelName
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8.dpToPx(), 0, 0) // optional margin above buttons
                }
                setOnClickListener {
                    hideFabMenu() // Hide menu after selecting a model
                    placeModel(modelName)
                }
            }
            fabMenu.addView(modelButton)
        }
    }

    private fun Int.dpToPx(): Int {
        val density = this@ModelPlacementActivity5.resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun hideFabMenu() {
        fabOpened = false
        fabMenu.visibility = View.GONE
        fabMenu.removeAllViews()
    }

    private fun placeModel(modelName: String) {
        arFragment.arSceneView.arFrame?.hitTest(getScreenCenter().x.toFloat(), getScreenCenter().y.toFloat())
            ?.firstOrNull { hit -> hit.trackable is Plane && (hit.trackable as Plane).isPoseInPolygon(hit.hitPose) }
            ?.let { hitResult ->
                anchorNode?.let {
                    arFragment.arSceneView.scene.removeChild(it)
                    it.anchor?.detach()
                }

                val newAnchor = hitResult.createAnchor()
                anchorNode = AnchorNode(newAnchor).apply {
                    setParent(arFragment.arSceneView.scene)
                }

                val modelRenderable = loadedModels[modelName]
                modelNode = TransformableNode(arFragment.transformationSystem).apply {
                    renderable = modelRenderable
                    setParent(anchorNode)
                    select()
                    scaleController.isEnabled = false
                    localScale = Vector3(0.5f, 0.5f, 0.5f) // Set fixed size

                }
                listAvailableAnimations()
                models[modelName]?.second?.let { allAnimations(it) }
            }
    }

    private fun allAnimations(animations: List<String>) {
        modelNode?.renderableInstance?.let { instance ->
            if (instance.animationCount > 0) {
                lifecycleScope.launch {
                    for ((index, animationName) in animations.withIndex()) {
                        // Introduce a delay before starting the next animation for "Battery Removal"
                        if (animationName == "2") {
                            delay(2000L) // Delay for 1 second (adjust as needed)
                        }

                        instance.animate(animationName).start()
                        // Optional: Log to track which animation is being played
                        Log.d("Animation", "Playing animation: $animationName at index $index")
                    }
                }
            }
        }
    }


    private fun preloadModels() {
        lifecycleScope.launch {
            for ((name, data) in models) {
                val modelPath = data.first
                try {
                    val modelRenderable = ModelRenderable.builder()
                        .setSource(this@ModelPlacementActivity5, Uri.parse(modelPath))
                        .setIsFilamentGltf(true)
                        .await()
                    loadedModels[name] = modelRenderable
                } catch (e: Exception) {
                    Log.e("ModelPlacementActivity5", "Error loading model: $modelPath", e)
                }
            }
        }
    }

    private fun getScreenCenter(): Point {
        val vw = arFragment.requireView().width
        val vh = arFragment.requireView().height
        return Point(vw / 2, vh / 2)
    }

    private fun listAvailableAnimations() {
        modelNode?.renderableInstance?.let { instance ->
            val animationCount = instance.animationCount
            Log.d("ModelAnimations", "Total animations: $animationCount")

            for (i in 0 until animationCount) {
                val animationName = instance.getAnimationName(i)
                Log.d("ModelAnimations", "Animation $i: $animationName")
            }
        } ?: Log.e("ModelPlacementActivity5", "Model instance is not initialized")
    }


}
