package com.scowluga.android.omr.request
import com.android.volley.toolbox.HttpHeaderParser

import com.android.volley.NetworkResponse

import com.android.volley.Request
import com.android.volley.Response

open class InputStreamVolleyRequest(method: Int, mUrl: String,
                                        private val mListener: Response.Listener<ByteArray>,
                                        errorListener: Response.ErrorListener,
                                        params: HashMap<String, String>) : Request<ByteArray>(method, mUrl, errorListener) {
    private val mParams: Map<String, String>

    //create a static map for directly accessing headers
    private lateinit var responseHeaders: Map<String, String>

    init {
        // this request would never use cache.
        setShouldCache(false)
        this.mParams = params
    }

    override fun deliverResponse(response: ByteArray) {
        mListener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers

        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }
}