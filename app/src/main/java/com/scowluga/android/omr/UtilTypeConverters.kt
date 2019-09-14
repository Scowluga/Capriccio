package com.scowluga.android.omr

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by david on 2019-09-14.
 */

class UtilTypeConverters {
    companion object {
        /**
         * Potential methods for integrating Room and SQLite
         * https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
         */
//        fun BitmapToStorage(bitmap: Bitmap): String = TODO()
//
//        fun BitmapFromStorage(string: String): Bitmap = TODO()
//
//        fun FileToStorage(file: File): String = TODO()
//
//        fun FileFromStorage(filename: String): File = TODO()


        /**
         * jsonObject: object returned by server
         * Convert it to a file
         */
        fun JSONObjectToFile(jsonObject: JSONObject): File = TODO()


        fun UriToBitmap(uri: Uri, activity: Activity) = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)

        fun BitmapToString(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }
    }
}