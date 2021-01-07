package com.syroniko.casseteapp.chatAndMessages.entities

class SpotifyTrackMessage(
    messageId: String = "",
    senderId: String = "",
    receiverId: String = "",
    timestamp: Long = 0,
    read: Boolean = false,
    val spotifyTrackId: String = "",
    val spotifyTrackName: String = "",
    val spotifyArtistNames: ArrayList<String> = arrayListOf()
) : Message(messageId, senderId, receiverId, timestamp, read, MessageType.SPOTIFY_TRACK){

    fun getSpotifyArtistsString(): String{
        val artistsString = spotifyArtistNames.fold(""){ acc, element ->
            "$acc$element, "
        }

        val length = artistsString.length
        return StringBuilder(artistsString)
            .deleteCharAt(length-1).deleteCharAt(length-2).toString()
    }
}