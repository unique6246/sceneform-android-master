package com.google.ar.sceneform.samples.gltf

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.DialogFragment

class FullScreenMediaDialogFragment : DialogFragment() {

    private var isVideo: Boolean = false
    private var mediaPath: String? = null

    companion object {
        fun newInstance(isVideo: Boolean, mediaPath: String): FullScreenMediaDialogFragment {
            val fragment = FullScreenMediaDialogFragment()
            val args = Bundle().apply {
                putBoolean("isVideo", isVideo)
                putString("mediaPath", mediaPath)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.black)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_fullscreen_media, container, false)

        isVideo = arguments?.getBoolean("isVideo") ?: false
        mediaPath = arguments?.getString("mediaPath")

        val imageView = view.findViewById<ImageView>(R.id.fullscreenImage)
        val videoView = view.findViewById<VideoView>(R.id.fullscreenVideo)

        val uri = Uri.parse(mediaPath)

        if (isVideo) {
            imageView.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener {
                it.isLooping = true
                videoView.start()
            }
        } else {
            videoView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(uri)
        }

        view.setOnClickListener { dismiss() }
        return view
    }
}
