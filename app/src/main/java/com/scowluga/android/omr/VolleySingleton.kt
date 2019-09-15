package com.scowluga.android.omr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.widget.ProgressBar
import com.scowluga.android.omr.request.InputStreamVolleyRequest
import java.io.File

/**
 * Created by david on 2019-09-14.
 */

class VolleySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: VolleySingleton(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun sendBitmapToServer(music: Music, activity: MainActivity) {
//        val progressBar = activity.findViewById<ProgressBar>(R.id.progress_bar)
//        progressBar.visibility = View.VISIBLE

        val url = "https://radiant-basin-00657.herokuapp.com/api/image"

        val jsonObject = JSONObject()
        jsonObject.put("image", UtilTypeConverters.BitmapToString(music.bitmap!!))

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { resultJsonObject ->
//                    progressBar.visibility = View.GONE
                    Log.d("MY_TAG", "Response: ${resultJsonObject.getString("fileName")}")
                    activity.resultFromServer(music, resultJsonObject)
                },
                Response.ErrorListener { error ->
//                    progressBar.visibility = View.GONE
                    Log.d("MY_TAG", "Error: $error")
                }
        )
        requestQueue.add(jsonObjectRequest)
    }

    fun getFileFromServer(fileName: String, name: String, context: Context) {
        val url = String.format("https://radiant-basin-00657.herokuapp.com/api/get_image/%s", fileName)

        val req = InputStreamVolleyRequest(Request.Method.GET, url, Response.Listener { res ->
            try {
                if (res != null) {
                    val outputStream = context.openFileOutput(name + ".mid", Context.MODE_PRIVATE)
                    outputStream.write(res)
                    outputStream.close()
                }
            } catch (e: Exception) {
                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE")
                e.printStackTrace()
            }
        }, Response.ErrorListener { e ->
            e.printStackTrace()
        }, HashMap())
        requestQueue.add(req)
    }
}