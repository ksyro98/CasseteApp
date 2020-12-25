package com.syroniko.casseteapp.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.R

fun addImage(
    context: Context,
    uid: String, imageView: ImageView,
    cropCircle: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val profileRef = Firebase.storage.reference.child("images/$uid.jpg")

    var baseRequestOption = Glide.with(context).load(profileRef).placeholder(R.drawable.boxicon)

    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()

    if (useSignature) {
        baseRequestOption = baseRequestOption.signature(
            MediaStoreSignature("image/jpeg", version, 0))
    }

    baseRequestOption.into(imageView)
}
fun addImageWithMetadata(
    context: Context,
    uid: String, imageView: ImageView, textView : TextView,
    cropCircle: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val profileRef = Firebase.storage.reference.child("images/$uid.jpg")

    var baseRequestOption = Glide.with(context).load(profileRef).placeholder(R.drawable.boxicon)
    profileRef.metadata.addOnSuccessListener { metadata ->
        // Metadata now contains the metadata for 'images/forest.jpg'
        val nameMetadata = metadata.getCustomMetadata("name").toString()
        var stringBuilderName = StringBuilder(nameMetadata)
        if(nameMetadata.length>9){
            stringBuilderName.setCharAt(8,'.')
            stringBuilderName.setCharAt(7,'.')
            stringBuilderName.setCharAt(9,'.')

        }
        textView.setText(stringBuilderName)
    }.addOnFailureListener {
        // Uh-oh, an error occurred!
    }
    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()

    if (useSignature) {
        baseRequestOption = baseRequestOption.signature(
            MediaStoreSignature("image/jpeg", version, 0))
    }

    baseRequestOption.into(imageView)
}