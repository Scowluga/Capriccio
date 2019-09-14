package com.scowluga.android.omr

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || data?.data == null) return

        val bitmap = UtilTypeConverters.UriToBitmap(data.data, this)
        VolleySingleton.getInstance(this).sendBitmapToServer(bitmap, this)
    }

    fun resultFromServer(bitmap: Bitmap, jsonObject: JSONObject) {
        findViewById<ImageView>(R.id.image_view).setImageBitmap(bitmap)
        MIDIManager.playMusic(jsonObject)
    }
}
