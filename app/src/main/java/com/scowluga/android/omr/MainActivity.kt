package com.scowluga.android.omr

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import org.json.JSONArray
import org.json.JSONObject
import android.R.attr.bitmap
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.upload_images).setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || data?.clipData == null) return

        val jsonObject = JSONObject()

        for (i in 0 until data.clipData.itemCount) {
            val uri = data.clipData.getItemAt(i).uri
            val bitmap = MyUtil.UriToBitmap(uri, this)



            // upload to server
            jsonObject.put("image", bitmap)
            VolleySingleton.getInstance(this).sendBitmapToServer(bitmap, this)
        }
    }
}
