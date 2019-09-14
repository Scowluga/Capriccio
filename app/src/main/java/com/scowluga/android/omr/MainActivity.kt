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

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
        }
    }

    private fun Uri.asBitmap(activity: Activity) = MediaStore.Images.Media.getBitmap(activity.contentResolver, this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || data?.clipData == null) return

        val ll = findViewById<LinearLayout>(R.id.linearLayout)
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        jsonObject.put("image_links", jsonArray)

        for (i in 0 until data.clipData.itemCount) {
            val uri = data.clipData.getItemAt(i).uri
            val bitmap = uri.asBitmap(this@MainActivity)

            // display on LinearLayout
            val imageView = ll.getChildAt(i) as? ImageView ?: continue
            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(bitmap)

            // add to jsonArray
            jsonArray.put(CloudinaryManager.translateToURL(bitmap))
        }

        VolleyManager.sendJsonRequest(jsonObject, this@MainActivity)
    }
}
