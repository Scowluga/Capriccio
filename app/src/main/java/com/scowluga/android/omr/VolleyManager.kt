package com.scowluga.android.omr

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by david on 2019-09-14.
 */

class VolleyManager {
    companion object {
        fun sendJsonRequest(jsonObject: JSONObject, context: Context) {
            val url = "https://radiant-basin-00657.herokuapp.com/api/test"

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    Response.Listener {
                        Log.d("MY_TAG", "Response: $it")
                        MIDIManager.playMusic(it)
                    },
                    Response.ErrorListener { error ->
                        Log.d("MY_TAG", "Error: $error")
                    }
            )

            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
        }
    }
}