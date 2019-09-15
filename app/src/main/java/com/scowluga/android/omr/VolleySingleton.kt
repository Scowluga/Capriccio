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
import android.widget.Toast
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
        val url = "https://radiant-basin-00657.herokuapp.com/api/image"

        val jsonObject = JSONObject()
        jsonObject.put("image", UtilTypeConverters.BitmapToString(music.bitmap!!))

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { resultJsonObject ->
                    Log.d("MY_TAG", "Response: ${resultJsonObject.getString("fileName")}")
                    activity.resultFromServer(music, resultJsonObject)
                },
                Response.ErrorListener { e ->
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        )
        requestQueue.add(jsonObjectRequest)
    }

    fun getFileFromServer(music: Music, fileName: String, activity: MainActivity) {
        val url = String.format("https://radiant-basin-00657.herokuapp.com/api/get_image/%s", fileName)

        val req = InputStreamVolleyRequest(Request.Method.GET, url,
                Response.Listener { res ->
                    try {
                        if (res != null) {
                            val outputStream = activity.openFileOutput(music.name + ".mid", Context.MODE_PRIVATE)
                            outputStream.write(res)
                            outputStream.close()
                        }
                        activity.result2FromServer(music)
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { e ->
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()

        }, HashMap())
        requestQueue.add(req)
    }
}