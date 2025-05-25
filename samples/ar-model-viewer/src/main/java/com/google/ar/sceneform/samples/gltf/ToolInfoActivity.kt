package com.google.ar.sceneform.samples.gltf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.sceneform.samples.gltf.adapter.ToolAdapter
import com.google.ar.sceneform.samples.gltf.model.MediaItem
import com.google.ar.sceneform.samples.gltf.model.MediaType
import com.google.ar.sceneform.samples.gltf.model.ToolItem

class ToolInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_info)

        val recyclerView = findViewById<RecyclerView>(R.id.toolRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tools = listOf(
            ToolItem(
                "Battery Removal", "Unlock the battery compartment by sliding lock latch.\n" +
                        "Open the cover to access the battery.\n" +
                        "Remove the battery by pressing the release button.\n" +
                        "Insert a charged Bosch 18V battery until it clicks.\n" +
                        "Close & lock the compartment securely.",
                listOf(
                    MediaItem(MediaType.VIDEO, R.raw.battery_removal)
                )
            ),
            ToolItem(
                "Power Button", "Press and hold the power button to turn on the tool.\n" +
                        "The tool automatically self-levels using internal sensors.\n" +
                        "A steady laser beam means leveling is successful.",
                listOf(
                    MediaItem(MediaType.IMAGE, R.drawable.automatic_levelling),
                    MediaItem(MediaType.VIDEO, R.raw.automatic_levelling)
                )
            ),
            ToolItem(
                    "Manual Operation", "Select Manual Mode button on the control panel.\n" +
                        "Adjust Slope using arrow keys for X and Y axes.\n" +
                        "Confirm to hold the set inclination and project the beam.",
                listOf(
                    MediaItem(MediaType.IMAGE, R.drawable.manual_levelling),
                    MediaItem(MediaType.IMAGE, R.drawable.manual_arrows),
                    MediaItem(MediaType.VIDEO, R.raw.manual_levelling)
                )
            ),
            ToolItem(
                "Rotational Speed", "Access Speed Settings via the control panel.\n" +
                        "Select Desired RPM using the same button (150, 300, 600).\n" +
                        "The laser will rotate at the chosen speed.",
                listOf(
                    MediaItem(MediaType.IMAGE, R.drawable.rotation_speed),
                    MediaItem(MediaType.IMAGE,R.drawable.rotational_speed_2),
                    MediaItem(MediaType.VIDEO, R.raw.rotational_speed)
                )
            ),
            ToolItem(
                "Angle Selection", "Activate Angle Mode from the control panel.\n" +
                        "Set the Desired Angle using the arrow keys.\n" +
                        "The laser beam will lock and project at the selected angle.",
                listOf(
                    MediaItem(MediaType.IMAGE, R.drawable.angle_selection),
                    MediaItem(MediaType.IMAGE, R.drawable.angle_arrows),
                    MediaItem(MediaType.VIDEO, R.raw.angle_selection)
                )
            ),
            ToolItem(
                "Bluetooth Connection", "Install the App from QR and open the app.\n" +
                        "Press the Bluetooth button on the laser\n" +
                        "Turn on Bluetooth in your phone’s settings.\n" +
                        "Select the device, and it will connect automatically.\n" +
                        "Use the app to adjust functions like slope, rotation speed, and angle.",
                listOf(
                    MediaItem(MediaType.IMAGE, R.drawable.levelling_remote_app_qr),
                    MediaItem(MediaType.IMAGE, R.drawable.bluetooth_connection),
                    MediaItem(MediaType.VIDEO, R.raw.bluetooth_connection)
                )
            ),

        )

        recyclerView.adapter = ToolAdapter(tools)
    }
}
