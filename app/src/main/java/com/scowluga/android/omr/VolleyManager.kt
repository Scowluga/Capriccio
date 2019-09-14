package com.scowluga.android.omr

import android.app.Activity
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by david on 2019-09-14.
 */

class VolleyManager {
    companion object {
        fun sendJsonRequest(activity: Activity) {
            val url = "https://radiant-basin-00657.herokuapp.com/api/test"

            val jsonObject = JSONObject()

            val jsonArray = JSONArray()

            jsonObject.put("Hello", "World")
//            jsonObject.put("whee", JSONArray())

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    Response.Listener {
                        Log.d("MY_TAG", "Response: $it")
                    },
                    Response.ErrorListener { error ->
                        Log.d("MY_TAG", "Error: $error")
                    }
            )

            // TODO: change to Singleton
            val requestQueue = Volley.newRequestQueue(activity)
            requestQueue.add(jsonObjectRequest)
        }





    }
}