package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.MainClasses.Cassette
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.MainClasses.addAndUpdate
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