package com.syroniko.casseteapp.firebase

import com.syroniko.casseteapp.firebasefirebase.CASSETTES
import com.syroniko.casseteapp.mainClasses.Cassette

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

//    override fun insert(item: Any) {
//        if (item !is Cassette){
//            return
//        }
//
//        dbCollection.add(item)
//            .addOnSuccessListener { documentReference ->
//                update(documentReference.id, hashMapOf(Pair("id", documentReference.id)))
//            }
//    }

    override fun insertWithCallback(item: Any, callback: (String) -> Unit) {
        if (item !is Cassette){
            return
        }

        dbCollection.add(item)
            .addOnSuccessListener { documentReference ->
                update(documentReference.id, hashMapOf(Pair("id", documentReference.id)))
                callback(documentReference.id)
            }
    }
}