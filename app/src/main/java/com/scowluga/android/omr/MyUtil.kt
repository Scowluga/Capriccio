package com.scowluga.android.omr

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Created by david on 2019-09-14.
 */

class MyUtil {
    companion object {
        fun UriToBitmap(uri: Uri, activity: Activity) = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)

        fun BitmapToString(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }
    }
}