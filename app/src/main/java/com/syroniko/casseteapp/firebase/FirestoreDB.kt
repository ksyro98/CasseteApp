package com.syroniko.casseteapp.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreDB(
    private val collectionName: String,
    protected val dbCollection: CollectionReference = FirebaseFirestore.getInstance().collection(collectionName)
)