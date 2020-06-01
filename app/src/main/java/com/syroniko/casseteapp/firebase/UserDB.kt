package com.syroniko.casseteapp.firebase

import com.syroniko.casseteapp.MainClasses.User

object UserDB: FirestoreDB(USERS) {

    fun addUser(uid: String, user: User) = dbCollection.document(uid).set(user)

}