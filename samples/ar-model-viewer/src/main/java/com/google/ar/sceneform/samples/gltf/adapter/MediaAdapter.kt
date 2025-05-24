package com.google.ar.sceneform.samples.gltf.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.sceneform.samples.gltf.FullScreenMediaDialogFragment
import com.google.ar.sceneform.samples.gltf.R
import com.google.ar.sceneform.samples.gltf.model.MediaItem
import com.google.ar.sceneform.samples.gltf.model.MediaType

class MediaAdapter(private val mediaList: List<MediaItem>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mediaImage)
        val videoView: VideoView = view.findViewById(R.id.mediaVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = mediaList[position]

        when (item.type) {
            MediaType.IMAGE -> {
                holder.imageView.visibility = View.VISIBLE
                holder.videoView.visibility = View.GONE
                holder.imageView.setImageResource(item.resourceId)
                // Add click listener for image
                holder.imageView.setOnClickListener {
                    val uri = Uri.parse("android.resource://${holder.itemView.context.packageName}/${item.resourceId}")
                    val dialog = FullScreenMediaDialogFragment.newInstance(
                        isVideo = false,
                        mediaPath = uri.toString()
                    )
                    dialog.show(
                        (holder.itemView.context as AppCompatActivity).supportFragmentManager,
                        "FullScreenImage"
                    )
                }
            }

            MediaType.VIDEO -> {
                holder.imageView.visibility = View.GONE
                holder.videoView.visibility = View.VISIBLE
                val uri = Uri.parse("android.resource://${holder.itemView.context.packageName}/${item.resourceId}")
                holder.videoView.setVideoURI(uri)
                holder.videoView.setOnPreparedListener {
                    it.isLooping = true
                    holder.videoView.start()
                }
                // Add click listener for video
                holder.videoView.setOnClickListener {
                    val dialog = FullScreenMediaDialogFragment.newInstance(
                        isVideo = true,
                        mediaPath = uri.toString()
                    )
                    dialog.show(
                        (holder.itemView.context as AppCompatActivity).supportFragmentManager,
                        "FullScreenVideo"
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = mediaList.size
}
