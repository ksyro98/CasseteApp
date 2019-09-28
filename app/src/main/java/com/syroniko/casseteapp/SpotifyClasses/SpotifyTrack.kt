package com.syroniko.casseteapp.SpotifyClasses

import android.os.Parcelable
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.syroniko.casseteapp.TrackSearchFlow.SearchRequest
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
class SpotifyTrack(val trackName: String, val trackId: String, val artistIds: ArrayList<String>, val artistNames: ArrayList<String>, val imageUrl: String, var genre: String?) : Parcelable
    , SpotifyResult {

    private val genreMap = HashMap<String, Int>()

    override fun getClass(): String {
        return track
    }

    fun getGenreFromArtists(queue: RequestQueue, token: String, action: (String?) -> Unit){
        val tempGenres = arrayListOf<String>()

        val searchRequests = arrayListOf<SearchRequest>()
        for(artistId  in artistIds){
            searchRequests.add(SearchRequest(
                Request.Method.GET,
                "https://api.spotify.com/v1/artists/$artistId",
                token,
                Response.Listener<JSONObject> { response ->
//                    Log.d("SpotifyTrack", response.toString())

                    val jsonObject = Gson().fromJson(response.toString(), JsonObject::class.java)
                    val genres = jsonObject.get("genres") as JsonArray
                    for (genre in genres){
                        tempGenres.add(genre.asString)
                    }

//                    Log.d("SpotifyTrack", tempGenres.toString())

                    getGenreFromList(tempGenres, action)
                },
                Response.ErrorListener { error -> Log.e("SpotifyTrack", "Volley Error", error) }
            ))
        }

        for(searchRequest in searchRequests) {
            queue.add(searchRequest)
        }

    }

    private fun getGenreFromList(tempGenres: ArrayList<String>, action: (String?) -> Unit){
        initializeGenreMap()

        for (genre in tempGenres){
            val subGenres = genre.toLowerCase().replace("hip hop", "hip-hop").split(" ")
            for (subGenre in subGenres){
                if (subGenre in genreMap){
                   genreMap[subGenre] = genreMap[subGenre]!!.plus(1)
                }
            }

//            Log.d("SpotifyTrack", genreMap.toString())
        }

        genre = genreMap.maxBy { it.value }?.key
        action(genre)

//        Log.d("SpotifyTrack", genre)
    }

    private fun initializeGenreMap(){
        genreMap["blues"] = 0
        genreMap["classical"] = 0
        genreMap["country"] = 0
        genreMap["electronic"] = 0
        genreMap["folk"] = 0
        genreMap["hip-hop"] = 0
        genreMap["jazz"] = 0
        genreMap["metal"] = 0
        genreMap["pop"] = 0
        genreMap["punk"] = 0
        genreMap["r&b"] = 0
        genreMap["rock"] = 0
        genreMap["soundtracks"] = 0
    }

    private fun getMaxFromMap(map: HashMap<String, Int>) : String{
        var maxKey = ""
        var maxValue = 0
        for (entry in map){
            if (entry.value >= maxValue){
                maxValue = entry.value
                maxKey = entry.key
            }
        }

        return maxKey
    }
}

