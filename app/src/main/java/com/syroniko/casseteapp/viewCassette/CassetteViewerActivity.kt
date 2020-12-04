package com.syroniko.casseteapp.viewCassette

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.cassetteIdExtraName
import com.syroniko.casseteapp.userIdExtraName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_cassette_viewer.*

const val CASSETTE_VIEWER_REQUEST_CODE = 314
const val RESULT_RESPONSE = 11
const val RESULT_FORWARD = 12

const val YT_API_KEY = "AIzaSyDK0u16JJxYenpBhQRte-CC5FHl0IcMIeM"
const val YT_SEARCH_URL_START = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
const val YT_SEARCH_URL_END = "&type=video&key=$YT_API_KEY"
const val YT_APP_WATCH_URL = "vnd.youtube:"
const val YT_WEB_WATCH_URL = "https://www.youtube.com/watch?v="

private const val NUM_PAGES = 3

@AndroidEntryPoint
class CassetteViewerActivity : AppCompatActivity() {
    private var fragment = Fragment()
    private val viewModel by viewModels<CassetteViewerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cassette_viewer)

        viewModel.cassetteId = intent.getStringExtra(cassetteIdExtraName) ?: return
        viewModel.senderId = intent.getStringExtra(userIdExtraName) ?: return

        viewModel.getCassette {
            val pagerAdapter = ScreenSlidePagerAdapter(this)
            cassetteViewPager.adapter = pagerAdapter
        }

    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            fragment = when (position) {
                0 -> {
                    CassetteMessageFragment()
                }
                1 -> {
                    CassetteTrackFragment()
                }
                else -> {
                    CassetteVideoFragment()
                }
            }

            return fragment
        }
    }


    fun finishWithResult(result: Int){
        val resultIntent = Intent()
        resultIntent.putExtra(cassetteIdExtraName, viewModel.cassetteId)
        resultIntent.putExtra(userIdExtraName, viewModel.senderId)
        setResult(result, resultIntent)
        finish()
    }
}
