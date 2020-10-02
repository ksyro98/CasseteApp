package com.syroniko.casseteapp.TrackSearchFlow


fun prepareTrackQuery(searchQuery: String): String {
    val searchKeyWord = searchQuery.replace(" ", "%20")

    return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
        "https://api.spotify.com/v1/search?q=$searchKeyWord&type=track"
    } else {
        "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=track"
    }
}


fun prepareArtistQuery(searchQuery: String): String{
    val searchKeyWord = searchQuery.replace(" ", "%20")

    return if (searchKeyWord.contains("-") || searchKeyWord.contains("*")) {
        "https://api.spotify.com/v1/search?q=$searchKeyWord&type=artist"
    } else {
        "https://api.spotify.com/v1/search?q=$searchKeyWord*&type=artist"
    }
}