package com.syroniko.casseteapp.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.syroniko.casseteapp.MainClasses.User

class UserDB: FirestoreDB(USERS) {

//    fun insert(uid: String, user: User) {
//        dbCollection.document(uid).set(user)
//    }

    override fun insert(item: Any) {
        if (item !is User || item.uid == null){
            return
        }
        dbCollection.document(item.uid!!).set(item)
    }

    override fun getId(): String {
        return USERS
    }

}