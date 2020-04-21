package com.syroniko.casseteapp.viewCassette

interface CassetteData {
    fun getInitialCassetteData(cassetteId: String?, senderId: String?)

    fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String)
}