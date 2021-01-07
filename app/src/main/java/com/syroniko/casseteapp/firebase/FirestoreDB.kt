package com.syroniko.casseteapp.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

abstract class FirestoreDB(private val collectionName: String){

    protected val db = Firebase.firestore
    protected val dbCollection = db.collection(collectionName)
    protected lateinit var registration: ListenerRegistration

    init {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    open fun insert(item: Any) {
        db.collection(collectionName).add(item)
    }

    open fun insertWithCallback(item: Any, callback: (String) -> Unit){
        db.collection(collectionName).add(item)
            .addOnSuccessListener {
                callback(it.id)
            }
    }

    open fun insertWithId(id: String, item: Any) {
        dbCollection.document(id).set(item)
    }

    open fun delete(id: String) {
        db.collection(collectionName).document(id).delete()
    }

    open fun listenToChanges(onChange: (List<DocumentSnapshot>) -> Unit) {
        val tag = FirestoreDB::class.java.simpleName

        registration = db.collection(collectionName).addSnapshotListener { snapshot, e ->
            if(e != null){
                Log.w(tag, "List failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty){
                onChange(snapshot.documents)
            }
            else{
                Log.d(tag, "Current data: null")
            }
        }
    }

    open fun detachListener(){
        if (::registration.isInitialized){
            registration.remove()
        }
    }

    open fun getDocumentFromId(id: String) = dbCollection.document(id).get()

    open fun update(id: String, updateMap: HashMap<String, Any>) =
        dbCollection.document(id).update(updateMap)

    abstract fun getId(): String

    companion object{
        fun getTime(): Long{
            return Timestamp.now().seconds
        }
    }

}