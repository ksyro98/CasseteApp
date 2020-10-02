package com.syroniko.casseteapp.firebase

import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.firebasefirebase.USERS

class UserDB: FirestoreDB(USERS) {

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