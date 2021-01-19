package com.syroniko.casseteapp.chatAndMessages.entities

import com.syroniko.casseteapp.spotifyClasses.SpotifyTrack

class SpotifyTrackMessage(
    messageId: String = "",
    senderId: String = "",
    receiverId: String = "",
    timestamp: Long = 0,
    read: Boolean = false,
    val spotifyTrack: SpotifyTrack = SpotifyTrack()
) : Message(messageId, senderId, receiverId, timestamp, read, MessageType.SPOTIFY_TRACK){

    fun getSpotifyArtistsString(): String{
        val artistsString = spotifyTrack.artistNames.fold(""){ acc, element ->
            "$acc$element, "
        }

        val length = artistsString.length
        return StringBuilder(artistsString)
            .deleteCharAt(length-1).deleteCharAt(length-2).toString()
    }
}