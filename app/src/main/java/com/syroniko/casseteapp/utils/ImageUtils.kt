package com.syroniko.casseteapp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.R

fun addImage(context: Context, uid: String, imageView: ImageView, cropCircle: Boolean = true){
    val profileRef = Firebase.storage.reference.child("images/$uid.jpg")

    if (cropCircle) {
        Glide.with(context)
            .load(profileRef)
            .circleCrop()
            .placeholder(R.drawable.boxicon)
            .into(imageView)
    }
    else{
        Glide.with(context)
            .load(profileRef)
            .placeholder(R.drawable.boxicon)
            .into(imageView)
    }
}