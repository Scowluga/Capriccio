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

    fun sendBitmapToServer(bitmap: Bitmap, activity: MainActivity) {
        val progressBar = activity.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        val url = "https://radiant-basin-00657.herokuapp.com/api/image"

        val jsonObject = JSONObject()
        jsonObject.put("image", UtilTypeConverters.BitmapToString(bitmap))

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { resultJsonObject ->
                    progressBar.visibility = View.GONE
                    Log.d("MY_TAG", "Response: ${resultJsonObject.getString("fileName")}")
                    activity.resultFromServer(bitmap, resultJsonObject)
                },
                Response.ErrorListener { error ->
                    progressBar.visibility = View.GONE
                    Log.d("MY_TAG", "Error: $error")
                }
        )
        requestQueue.add(jsonObjectRequest)
    }
}