package com.syroniko.casseteapp.SpotifyClasses

val GENRES = arrayOf(
        "Blues",
        "Classical",
        "Country",
        "Electronic",
        "Folk",
        "Hip-Hop",
        "Jazz",
        "Metal",
        "Pop",
        "Punk",
        "R&B",
        "Rock",
        "Soundtracks")

fun mapGenres(genre: String?) : String?{
        return when(genre){
                "blues" -> "Blues"
                "classical" -> "Classical"
                "country" -> "Country"
                "electronic" -> "Electronic"
                "folk" -> "Folk"
                "hip-hop" -> "Hip-Hop"
                "jazz" -> "Jazz"
                "metal" -> "Metal"
                "pop" -> "Pop"
                "punk" -> "Punk"
                "r&b" -> "R&B"
                "rock" -> "Rock"
                "soundtracks" -> "Soundtracks"
                else -> genre
        }
}