package com.google.ar.sceneform.samples.gltf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.sceneform.samples.gltf.R
import com.google.ar.sceneform.samples.gltf.model.ToolItem

class ToolAdapter(private val tools: List<ToolItem>) :
    RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    inner class ToolViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.toolName)
        val desc: TextView = view.findViewById(R.id.toolDescription)
        val mediaRecycler: RecyclerView = view.findViewById(R.id.mediaRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tool, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = tools[position]
        holder.name.text = tool.name
        holder.desc.text = tool.description

        holder.mediaRecycler.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.mediaRecycler.adapter = MediaAdapter(tool.mediaList)
    }

    override fun getItemCount(): Int = tools.size
}
