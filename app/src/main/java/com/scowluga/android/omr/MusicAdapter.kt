package com.scowluga.android.omr

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by david on 2018-07-24.
 */

class MusicAdapter(var musicList: List<Music>, var activity: Activity) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameTv: TextView
        var circularIv: CircleImageView

        init {
            nameTv = itemView.findViewById<TextView>(R.id.name_tv)
            circularIv = itemView.findViewById<CircleImageView>(R.id.circular_image)
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
        holder.circularIv.setImageBitmap(music.bitmap)

        holder.itemView.setOnClickListener {
            if (music.file == null) return@setOnClickListener

            val uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", music.file!!)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}
