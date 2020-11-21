package com.syroniko.casseteapp.utils

import android.content.Context
import android.widget.ImageView
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