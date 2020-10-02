package com.syroniko.casseteapp.viewCassette

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.TrackSearchFlow.NO_PREVIEW_URL
import com.syroniko.casseteapp.cassetteIdExtraName
import com.syroniko.casseteapp.userIdExtraName
import kotlinx.android.synthetic.main.activity_cassette_viewer.*


const val resultForward = 12
const val resultReply = 11
const val resultCassette = "result cassette"
const val requestTag = "YouTubeTag"
const val ytApiKey = "AIzaSyDK0u16JJxYenpBhQRte-CC5FHl0IcMIeM"
const val ytSearchUrlStart = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
const val ytSearchUrlEnd = "&type=video&key=$ytApiKey"
const val ytAppWatchUrl = "vnd.youtube:"
const val ytWebWatchUrl = "https://www.youtube.com/watch?v="

private const val NUM_PAGES = 3

class CassetteViewerActivity : AppCompatActivity() {
    private var trackPreviewUrl = NO_PREVIEW_URL
    private var cassetteComment: String? = null
    private var trackName = ""
    private var trackId = ""
    private var cassetteId: String? = null
    private var senderId: String? = null

    private var fragment = Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cassette_viewer)

        cassetteId = intent.getStringExtra(cassetteIdExtraName)
        senderId = intent.getStringExtra(userIdExtraName)

        val db = FirebaseFirestore.getInstance()

        val localCassetteId = cassetteId.toString()
        db.collection("cassettes")
            .document(localCassetteId)
            .get()
            .addOnSuccessListener { document ->
                cassetteComment = document.data?.get("comment") as String

                val trackMap = document.data?.get("track") as Map<*, *>
                trackName = trackMap["trackName"] as String
                trackId = trackMap["trackId"] as String
                if(trackMap["previewUrl"] != null) {
                    trackPreviewUrl = trackMap["previewUrl"] as String
                }

                if (fragment is CassetteData){
                    (fragment as CassetteData).getCassetteDataFromDb(cassetteComment.toString(), trackName, trackId, trackPreviewUrl)
                }
            }

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        cassetteViewPager.adapter = pagerAdapter
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

            (fragment as CassetteData).getInitialCassetteData(cassetteId, senderId)
            if (cassetteComment != null){
                (fragment as CassetteData).getCassetteDataFromDb(cassetteComment.toString(), trackName, trackId, trackPreviewUrl)
            }

            return fragment
        }
    }
}
