package com.scowluga.android.omr

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.gson.Gson
import com.scowluga.android.omr.dialog.DialogActivity
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {
    companion object {
        val REQUEST_CODE = 1

        // if MainActivity is running
        var isRunning: Boolean = false

        var appBarExpanded = true
        var fabShown = true
        var isAnimating = false

        // tells DialogActivity whether to animate from toolbar of fab
        var isAddBtnClick = false

        var musicList: MutableList<Music> = ArrayList()
    }

    // for display of routineList
    lateinit var rv: RecyclerView
    var adapter: MusicAdapter = MusicAdapter(musicList, this)

    // views for parallax header
    lateinit var menuAddBtn: ImageButton
    lateinit var collapseMenu: Menu
    lateinit var collapsingToolbar: CollapsingToolbarLayout
    lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set status bar transparent for parallax toolbar
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        // setting toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // setting expandability
        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) > 200) {
                appBarExpanded = false
                invalidateOptionsMenu()
            } else {
                appBarExpanded = true
                invalidateOptionsMenu()
            }
        }

        collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        // open dialog from fab
        fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Add new routine
            isAddBtnClick = false

            val intent = Intent(this@MainActivity, DialogActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, fab, getString(R.string.transition_dialog))
            startActivityForResult(intent, MainActivity.REQUEST_CODE, options.toBundle())
        }


        // open dialog from menu
        menuAddBtn = toolbar.findViewById<ImageButton>(R.id.action_add)
        menuAddBtn.setOnClickListener {
            isAddBtnClick = true

            val intent = Intent(this@MainActivity, DialogActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, menuAddBtn, getString(R.string.transition_dialog))
            startActivityForResult(intent, MainActivity.REQUEST_CODE, options.toBundle())
        }

        // init recycler view
        rv = findViewById<RecyclerView>(R.id.recyclerView)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this@MainActivity)

        rv.itemAnimator = SlideInDownAnimator()
        rv.itemAnimator.addDuration = 500
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != MainActivity.REQUEST_CODE
                || resultCode != Activity.RESULT_OK
                || data == null) return

        val name = data.getStringExtra(DialogActivity.RETURN_MUSIC)
        val bitmap = DialogActivity.bitmap!!

        val music = Music(name, bitmap, null)

        Handler().postDelayed({
            musicList.add(0, music)
            adapter.notifyItemInserted(0)
        }, 100)

        VolleySingleton.getInstance(this).sendBitmapToServer(music, this)
    }

    fun resultFromServer(music: Music, jsonObject: JSONObject) {
        val fileName = jsonObject.getString("fileName")
        val file = File(filesDir, music.name + ".mid")
        music.file = file

        VolleySingleton.getInstance(this).getFileFromServer(music, fileName, this)
    }

    fun result2FromServer(music: Music) {
        adapter.notifyItemChanged(musicList.indexOf(music))
    }

    // ----- Lifecycle for isRunning -----

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
    }

    // ----- Menu -----

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        collapseMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.test) {
            // nothing
        }
        return super.onOptionsItemSelected(item)
    }

    // variables for animation of collapsing toolbar
    private var fabX: Float = 0.toFloat()
    private var fabY: Float = 0.toFloat()
    private var menuX: Float = 0.toFloat()
    private var menuY: Float = 0.toFloat()
    private var translateX: Float = 0.toFloat()
    private var translateY: Float = 0.toFloat()
    private val animDuration: Long = 100

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val f = findViewById<FrameLayout>(R.id.frameLayout)

        if (collapseMenu != null && !appBarExpanded) {
            // collapsed
            if (fabShown && !isAnimating) {
                // and fab is showing
                // means -> animate fab to menuitem

                // get absolute x & y
                val info = IntArray(2)
                fab.getLocationInWindow(info)
                fabX = info[0].toFloat()
                fabY = info[1].toFloat()

                menuAddBtn.getLocationInWindow(info)
                menuX = info[0].toFloat()
                menuY = info[1].toFloat()

                // calculation for how much to translate
                translateX = menuX - fabX
                translateY = menuY - fabY

                // scale it out
                f.animate().scaleX(0f).setDuration(animDuration).start()
                f.animate().scaleY(0f).setDuration(animDuration).start()

                // translate
                f.animate().translationXBy(translateX).setDuration(animDuration).setInterpolator(DecelerateInterpolator()).start()
                f.animate().translationYBy(translateY).setDuration(animDuration).start()

                // handler for after the scale & translation animations
                val h = Handler()
                h.postDelayed({
                    // swap visibilities of fab and menu item
                    fab.setVisibility(View.GONE)
                    menuAddBtn.setVisibility(View.VISIBLE)

                    menuAddBtn.setScaleX(0f)
                    menuAddBtn.setScaleY(0f)

                    // animate in menu item
                    menuAddBtn.animate().scaleX(1f).setDuration(animDuration).start()
                    menuAddBtn.animate().scaleY(1f).setDuration(animDuration).start()

                    // set flags
                    fabShown = false
                    isAnimating = false
                }, animDuration)
                isAnimating = true
            }

            if (isAnimating) {
                // if it's in that 100 (animDuration) while animating
                // we want ti just keep the menu item invisible
                menuAddBtn.setVisibility(View.INVISIBLE)
            }

        } else {
            // not collapsed
            menuAddBtn.setVisibility(View.INVISIBLE)
            if (!fabShown) {
                // fab not shown
                // means -> animate fab from menu item

                // scale out
                f.animate().scaleX(1f).setDuration(animDuration).start()
                f.animate().scaleY(1f).setDuration(animDuration).start()

                // translate (we already have translation values from before)
                f.animate().translationXBy(-translateX).setDuration(animDuration).setInterpolator(AccelerateInterpolator()).start()
                f.animate().translationYBy(-translateY).setDuration(animDuration).start()

                // no need for handler, just set visible
                fab.setVisibility(View.VISIBLE)
                fabShown = true
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }
}
