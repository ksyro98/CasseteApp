package com.syroniko.casseteapp.utils

import android.app.Activity
import android.content.Context


fun isFirstTime(activity: Activity) = readBoolFromPreferences(activity, KeyType.FirstTime)

fun markFirstTime(activity: Activity) = writeBoolInPreference(activity, KeyType.FirstTime)

fun hasDeniedSpotifyConnection(activity: Activity) = !readBoolFromPreferences(activity, KeyType.SpotifyEnabled)

fun denySpotifyConnection(activity: Activity) = writeBoolInPreference(activity, KeyType.SpotifyEnabled)


/**
 * Expected shared preferences values.
 * Spotify Enabled: False --> Spotify disabled. True --> Spotify enabled.
 * First Time: False --> Not the first time, True --> First time
 */
private const val FIRST_TIME_KEY = "first_time_key"
private const val SPOTIFY_CONNECTION_KEY = "spotify_connection_key"

private enum class KeyType {
    SpotifyEnabled,
    FirstTime
}

private fun readBoolFromPreferences(activity: Activity, keyType: KeyType): Boolean{
    val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE) ?: return true
    val key = mapKeyType(keyType)

    return sharedPreferences.getBoolean(key, true)
}

private fun writeBoolInPreference(activity: Activity, keyType: KeyType){
    val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE) ?: return
    val key = mapKeyType(keyType)

    with(sharedPreferences.edit()) {
        putBoolean(key, false)
        apply()
    }
}

private fun mapKeyType(keyType: KeyType) = when (keyType) {
    KeyType.SpotifyEnabled -> SPOTIFY_CONNECTION_KEY
    KeyType.FirstTime -> FIRST_TIME_KEY
}
