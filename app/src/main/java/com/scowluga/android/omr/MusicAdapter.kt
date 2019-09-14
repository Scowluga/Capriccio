package com.scowluga.android.omr

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by david on 2018-07-24.
 */

class MusicAdapter(var musicList: List<Music>, var activity: Activity) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTv: TextView


        init {
            nameTv = itemView.findViewById<TextView>(R.id.nameTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(activity)
        val rowView = inflater.inflate(R.layout.layout_music_list_item, parent, false)
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // setting up views
        val music = musicList.get(position)
        holder.nameTv.setText(music.name)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}
