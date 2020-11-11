package com.syroniko.casseteapp.utils

import android.util.Log
import com.spotify.sdk.android.authentication.AuthenticationResponse

const val SPOTIFY_REQUEST_CODE = 1337
const val CLIENT_ID = "846a7d470725449994155b664cb7959b"
const val REDIRECT_URI = "https://duckduckgo.com"
const val SPOTIFY_NO_TOKEN = "spotify_no_token"
private const val TAG = "SpotifyUtils"

fun onSpotifyResponse(response: AuthenticationResponse, callback: (String) -> Unit){
    when (response.type) {
        AuthenticationResponse.Type.TOKEN -> {
            Log.d(TAG, "token")
            callback(response.accessToken)
        }
        AuthenticationResponse.Type.ERROR -> {
            Log.d(TAG, "error")
            Log.e(TAG, "spotify error" + response.error)
        }
        AuthenticationResponse.Type.CODE -> Log.d(TAG, "code")
        AuthenticationResponse.Type.EMPTY -> Log.d(TAG, "empty")
        AuthenticationResponse.Type.UNKNOWN -> Log.d(TAG, "unknown")
        else -> Log.d(TAG, "null")
    }
}