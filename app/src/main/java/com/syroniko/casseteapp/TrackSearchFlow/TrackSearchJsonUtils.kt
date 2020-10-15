package com.syroniko.casseteapp.TrackSearchFlow

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.syroniko.casseteapp.SpotifyClasses.SpotifyAlbum
import com.syroniko.casseteapp.SpotifyClasses.SpotifyArtist
import com.syroniko.casseteapp.SpotifyClasses.SpotifyTrack

const val NO_PREVIEW_URL = ""

fun getTrackFromJson(trackJson: JsonElement, albumImageUrl: String? = null): SpotifyTrack{
    val jsonTrack = trackJson as JsonObject
    val trackName = jsonTrack.get("name").asString

    val trackId = jsonTrack.get("id").asString

    val artistIds = arrayListOf<String>()
    val artistNames = arrayListOf<String>()
    for (artist in jsonTrack.getAsJsonArray("artists")) {
        artistIds.add((artist as JsonObject).get("id").asString)
        artistNames.add(artist.get("name").asString)
    }

    val imageUrl = albumImageUrl
        ?: (jsonTrack.getAsJsonObject("album").getAsJsonArray("images")[0] as JsonObject)
            .get("url").asString

    val previewUrl: String? = if (jsonTrack.get("preview_url").toString() != "null") {
        jsonTrack.get("preview_url").asString
    }
    else{
        NO_PREVIEW_URL
    }

    return SpotifyTrack(
            trackName,
            trackId,
            artistIds,
            artistNames,
            imageUrl,
            null,
            previewUrl
        )
}


fun getArtistFromJson(artist: JsonElement): SpotifyArtist{
    val jsonArtist = artist as JsonObject

    val artistName = jsonArtist.get("name").asString

    val artistId = jsonArtist.get("id").asString

    val imageUrl = if (jsonArtist.getAsJsonArray("images").size() > 0) {
        (jsonArtist.getAsJsonArray("images")[0] as JsonObject).get("url").asString
    } else {
        ""
    }

    return SpotifyArtist(artistName, artistId, imageUrl)
}


fun getAlbumFromJson(album: JsonElement): SpotifyAlbum{
    val jsonAlbum = album as JsonObject
    val albumName = jsonAlbum.get("name").asString
    val albumId = jsonAlbum.get("id").asString
    val imageUrl = (jsonAlbum.getAsJsonArray("images")[0] as JsonObject).get("url").asString

    return SpotifyAlbum(albumName, albumId, imageUrl)
}