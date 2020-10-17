package com.syroniko.casseteapp.firebase

import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.firebasefirebase.CASSETTES

class CassetteDB: FirestoreDB(CASSETTES) {

    override fun getId(): String {
        return CASSETTES
    }

}