package com.google.ar.sceneform.samples.gltf

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
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
        "Automatic Levelling" to Pair("models/Automatic Levelling.glb", listOf("Colorful glowing arrow.08Action", "Laser cameraAction", "PlaneAction")),
        "Manual Operation" to Pair("models/Manual Operation.glb", listOf("Arrow 1Action.001", "PlaneAction.001")),
        "Angle Selection" to Pair("models/Angle Selection.glb", listOf("Arrow 1Action.003", "Laser cameraAction.002", "Plane.001Action")),
        "Rotational Operation" to Pair("models/RO.glb", listOf("Arrow 1Action.002", "Laser cameraAction.001", "PlaneAction", "Plane.001Action")),
        "Battery Removal" to Pair("models/BR.glb", listOf("Closing LidAction.001","Open Latch.001Action.001","Laser cameraAction","Battery  v6Action"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        fab = findViewById(R.id.fab)
        fabMenu = findViewById(R.id.fab_menu)

        // Check if the tutorial has been shown before
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isTutorialCompleted = sharedPreferences.getBoolean("TutorialCompleted", false)

        if (!isTutorialCompleted) {
            // Show the tutorial overlay if it's the first time or tutorial isn't completed
            val tutorialOverlay = layoutInflater.inflate(R.layout.tutorial_overlay, null)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            addContentView(tutorialOverlay, params)

            val skipButton = tutorialOverlay.findViewById<Button>(R.id.skip_button)
            val nextButton = tutorialOverlay.findViewById<Button>(R.id.next_button)

            skipButton.setOnClickListener {
                // Hide tutorial overlay and mark it as completed
                tutorialOverlay.visibility = View.GONE
                sharedPreferences.edit().putBoolean("TutorialCompleted", true).apply()
                enableFabMenuInteraction() // Enable FAB menu interaction after tutorial ends
            }

            nextButton.setOnClickListener {
                tutorialStep++
                if (tutorialStep >= 2) {
                    tutorialOverlay.visibility = View.GONE
                    sharedPreferences.edit().putBoolean("TutorialCompleted", true).apply()
                    enableFabMenuInteraction() // Enable FAB menu interaction after tutorial ends
                } else {
                    showTutorials()
                }
            }

            showTutorials()
        } else {
            // Proceed to normal AR setup if tutorial is completed
            preloadModels()
            enableFabMenuInteraction() // Ensure FAB menu is enabled
        }
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

    private fun showTutorials() {
        if (tutorialOverlay == null) {
            tutorialOverlay = layoutInflater.inflate(R.layout.tutorial_overlay, null)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            addContentView(tutorialOverlay, params)
        }

        val highlightArrow = tutorialOverlay?.findViewById<ImageView>(R.id.highlight_arrow)
        val tutorialText = tutorialOverlay?.findViewById<TextView>(R.id.tutorial_text)
        val skipButton = tutorialOverlay?.findViewById<Button>(R.id.skip_button)
        val nextButton = tutorialOverlay?.findViewById<Button>(R.id.next_button)

        tutorialOverlay?.bringToFront() // Ensure it is on top

        skipButton?.setOnClickListener {
            (tutorialOverlay?.parent as? ViewGroup)?.removeView(tutorialOverlay)
            tutorialOverlay = null
            val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("TutorialCompleted", true).apply()
            enableFabMenuInteraction() // Enable FAB menu interaction after tutorial ends
        }

        nextButton?.setOnClickListener {
            tutorialStep++
            if (tutorialStep >= 2) {
                (tutorialOverlay?.parent as? ViewGroup)?.removeView(tutorialOverlay)
                tutorialOverlay = null
                val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("TutorialCompleted", true).apply()
                enableFabMenuInteraction() // Enable FAB menu interaction after tutorial ends
            } else {
                showTutorials()
            }
        }

        when (tutorialStep) {
            0 -> {
                tutorialText?.text = "Move your device to detect a plane"
            }
            1 -> {
                highlightArrow?.setImageResource(R.drawable.arrow)
                tutorialText?.text = "Tap the button to list features"

                // Make sure `fab` is not null and check its layout params
                val fab = tutorialOverlay?.findViewById<FloatingActionButton>(R.id.fab)
                if (fab != null && fab.layoutParams is FrameLayout.LayoutParams) {
                    val arrowWidth = highlightArrow?.width ?: 0
                    val fabParams = fab.layoutParams as FrameLayout.LayoutParams

                    // Adjust arrow position: X offset next to FAB, Y offset same as FAB
                    val xOffset = fab.x + fab.width // Position next to the FAB
                    val yOffset = fab.y // Align with FAB vertically

                    val arrowParams = FrameLayout.LayoutParams(arrowWidth, arrowWidth)
                    arrowParams.leftMargin = xOffset.toInt()
                    arrowParams.topMargin = yOffset.toInt()

                    highlightArrow?.layoutParams = arrowParams
                    highlightArrow?.visibility = View.VISIBLE
                } else {
                    Log.e("ModelPlacementActivity5", "FAB or its layoutParams is null")
                }
            }
            2 -> {
                tutorialText?.text = "Select the feature to experience"
            }
        }
    }



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
                }
                listAvailableAnimations()
                models[modelName]?.second?.let { allAnimations(it) }
            }
    }

    private fun allAnimations(animations: List<String>) {
        modelNode?.renderableInstance?.let { instance ->
            if (instance.animationCount > 0) {
                lifecycleScope.launch {
                    for (animationName in animations) {
                        instance.animate(animationName).start()
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

    private var tutorialStep = 0
    private var tutorialOverlay: View? = null
}
