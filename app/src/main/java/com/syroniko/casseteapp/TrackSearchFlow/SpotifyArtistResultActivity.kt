package com.syroniko.casseteapp.TrackSearchFlow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.syroniko.casseteapp.MainClasses.spotifyArtistResultExtraName
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.MainClasses.tokenExtraName
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import kotlinx.android.synthetic.main.activity_spotify_artist_result.*
import org.json.JSONObject

class SpotifyArtistResultActivity : AppCompatActivity() {

    private val albumList = arrayListOf<SpotifyAlbum>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_artist_result)

        if(intent == null){
            return
        }

        val artistName = intent.getStringExtra(spotifyArtistResultExtraName)?.replace(" ", "%20")
        val token = intent.getStringExtra(tokenExtraName) ?: return

        val query = "https://api.spotify.com/v1/search?q=artist%3A$artistName&type=album"

        val albumAdapter = AlbumAdapter(this, albumList, token)

        val context = this

        val queue = Volley.newRequestQueue(this)

        val searchRequest = SearchRequest(
            Request.Method.GET,
            query,
            token,
            Response.Listener<JSONObject> { response ->

                Log.d("ArtistResult", response.toString())
//                toast(response.toString())

                for (i in 0 until response.getJSONObject("albums").getJSONArray("items").length()) {
                    val item: JSONObject = response.getJSONObject("albums").getJSONArray("items").getJSONObject(i)
                    val albumId = item.getString("id")
                    val albumName = item.getString("name")
                    val imageUrl = item.getJSONArray("images").getJSONObject(0).getString("url")

                    albumList.add(SpotifyAlbum(albumName, albumId, imageUrl))
                }

                albumAdapter.notifyDataSetChanged()

            },
            Response.ErrorListener { error ->
                toast("Error. :("); Log.d(
                "ArtistResult",
                error.toString()
            ); toast(error.toString())
            })

        queue.add(searchRequest)


        spotifyAlbumRecyclerView.layoutManager = GridLayoutManager(this, 2) //as RecyclerView.LayoutManager?
        spotifyAlbumRecyclerView.setHasFixedSize(true)

        spotifyAlbumRecyclerView.adapter = albumAdapter

    }

}
