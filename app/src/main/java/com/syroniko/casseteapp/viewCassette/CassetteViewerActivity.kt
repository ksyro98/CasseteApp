package com.syroniko.casseteapp.viewCassette

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.NO_PREVIEW_URL
import com.syroniko.casseteapp.cassetteIdExtraName
import com.syroniko.casseteapp.userIdExtraName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_cassette_viewer.*


const val resultForward = 12
const val resultCassette = "result cassette"
const val ytApiKey = "AIzaSyDK0u16JJxYenpBhQRte-CC5FHl0IcMIeM"
const val ytSearchUrlStart = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
const val ytSearchUrlEnd = "&type=video&key=$ytApiKey"
const val ytAppWatchUrl = "vnd.youtube:"
const val ytWebWatchUrl = "https://www.youtube.com/watch?v="

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

        viewModel.getCassette { cassetteComment, trackName, trackId, trackPreviewUrl ->
            if (fragment is CassetteData){
                (fragment as CassetteData).getCassetteDataFromDb(cassetteComment, trackName, trackId, trackPreviewUrl)
            }

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

            (fragment as CassetteData).getInitialCassetteData(viewModel.cassetteId, viewModel.senderId)
            (fragment as CassetteData).getCassetteDataFromDb(
                viewModel.cassetteComment,
                viewModel.trackName,
                viewModel.trackId,
                viewModel.trackPreviewUrl
            )

            return fragment
        }
    }
}
