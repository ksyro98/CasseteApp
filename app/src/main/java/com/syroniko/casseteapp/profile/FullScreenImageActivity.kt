package com.syroniko.casseteapp.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.utils.addImage
import kotlinx.android.synthetic.main.activity_full_screen_image.*

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val uid = intent.getStringExtra(FULL_SCREEN_UID_EXTRA) ?: return

        addImage(this, uid, full_screen_image_view, false)

        full_screen_image_view.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        private const val FULL_SCREEN_UID_EXTRA = "full_screen_uid_extra"

        fun startActivity(context: Context, uid: String){
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra(FULL_SCREEN_UID_EXTRA, uid)
            context.startActivity(intent)
        }
    }

}