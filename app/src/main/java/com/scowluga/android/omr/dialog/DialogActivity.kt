package com.scowluga.android.omr.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.transition.ArcMotion
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import com.google.gson.Gson
import com.scowluga.android.omr.MainActivity
import com.scowluga.android.omr.Music
import com.scowluga.android.omr.R
import com.scowluga.android.omr.UtilTypeConverters

class DialogActivity : AppCompatActivity() {
    companion object {
        val RETURN_MUSIC = "RETURN_MUSIC"
        val REQUEST_CODE = 2

        var bitmap: Bitmap? = null
    }

    lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        // setup
        container = findViewById<ViewGroup>(R.id.container)
        setupTransition()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // dismiss button
        val dismissListener = View.OnClickListener { dismiss() }
        container.findViewById<Button>(R.id.button3).setOnClickListener(dismissListener)

        // button for selecting file
        findViewById<Button>(R.id.button_file).setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), DialogActivity.REQUEST_CODE)
        }

        // save button
        val addButton = container.findViewById<Button>(R.id.button2)
        addButton.isEnabled = false
        addButton.setOnClickListener {
            // check that all fields are valid
            var complete = true
            val nameTV = findViewById<TextInputLayout>(R.id.input_name)
            val nameStr = nameTV.editText?.text.toString()
            if (nameStr == null || nameStr.isEmpty()) {
                complete = false
                nameTV.error = "Field Required"
            } else {
                nameTV.error = null
            }

            if (bitmap == null) complete = false

            if (!complete) return@setOnClickListener

            val returnIntent = Intent()
            returnIntent.putExtra(RETURN_MUSIC, nameStr)
            setResult(Activity.RESULT_OK, returnIntent)
            finishAfterTransition()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != DialogActivity.REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || data?.data == null) return

        container.findViewById<Button>(R.id.button2).isEnabled = true
        bitmap = UtilTypeConverters.UriToBitmap(data.data, this)
    }

    fun setupTransition() {
        // don't super understand this
        // i think come back to it to read it
        // but for now just accept that it works :)
        val arcMotion = ArcMotion()
        arcMotion.minimumHorizontalAngle = 50f
        arcMotion.minimumVerticalAngle = 50f

        val easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in)

        val sharedEnter = MorphFabToDialog()
        sharedEnter.pathMotion = arcMotion
        sharedEnter.interpolator = easeInOut

        val sharedReturn = MorphDialogToFab()
        sharedReturn.pathMotion = arcMotion
        sharedReturn.interpolator = easeInOut

        if (container != null) {
            sharedEnter.addTarget(container)
            sharedReturn.addTarget(container)
        }
        window.sharedElementEnterTransition = sharedEnter
        window.sharedElementReturnTransition = sharedReturn
    }

    override fun onBackPressed() {
        dismiss()
    }

    fun dismiss() {
        setResult(Activity.RESULT_CANCELED)
        finishAfterTransition()
    }
}
