package com.syroniko.casseteapp

const val track = "SpotifyTrack"
const val artist = "SpotifyArtist"
const val separator = "SpotifySeparator"


interface SpotifyResult{
    fun getClass(): String
}