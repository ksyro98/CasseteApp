package com.syroniko.casseteapp.SpotifyClasses

const val track = "SpotifyTrack"
const val artist = "SpotifyArtist"
const val separator = "SpotifySeparator"
const val album = "SpotifyAlbum"


interface SpotifyResult{
    fun getClass(): String
}