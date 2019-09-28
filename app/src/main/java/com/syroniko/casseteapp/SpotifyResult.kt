package com.syroniko.casseteapp

const val track = "SpotifyTrack"
const val artist = "SpotifyArtist"
const val separator = "SpotifySeparator"
const val album = "SpotifyAlbum"


interface SpotifyResult{
    fun getClass(): String
}