package com.syroniko.casseteapp.firebase

import com.syroniko.casseteapp.firebasefirebase.CASSETTES

object CassetteDB: FirestoreDB(CASSETTES) {

    override fun getId(): String {
        return CASSETTES
    }

    fun getOneCassetteForUser(uid: String) =
        dbCollection
            .whereArrayContains("possibleReceivers", uid)
            .whereEqualTo("received", false)
            .limit(1)
            .get()

}