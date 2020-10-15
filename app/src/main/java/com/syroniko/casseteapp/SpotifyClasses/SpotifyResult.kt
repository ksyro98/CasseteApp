package com.syroniko.casseteapp.SpotifyClasses

const val TRACK = "SpotifyTrack"
const val ARTIST = "SpotifyArtist"
const val SEPARATOR = "SpotifySeparator"
const val ALBUM = "SpotifyAlbum"


interface SpotifyResult{
    fun getSpotifyClass(): String
}