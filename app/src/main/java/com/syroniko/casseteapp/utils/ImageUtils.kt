package com.syroniko.casseteapp.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.mainClasses.longToast


const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_FILE = 2

fun addImageWithPath(
    context: Context,
    path: String,
    imageView: ImageView,
    cropCircle: Boolean = true,
    roundCorners: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val imageRef = Firebase.storage.reference.child(path)

    var baseRequestOption = Glide.with(context).load(imageRef).placeholder(R.drawable.boxicon)
        .fitCenter()

    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()

    if (roundCorners) {
        baseRequestOption = baseRequestOption
            .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
    }

    if (useSignature) {
        baseRequestOption = baseRequestOption.signature(
            MediaStoreSignature("image/jpeg", version, 0)
        )
    }

    baseRequestOption.into(imageView)
}


fun addImage(
    context: Context,
    uid: String,
    imageView: ImageView,
    cropCircle: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val path = "images/$uid.jpg"
    addImageWithPath(context, path, imageView, cropCircle, false, useSignature, version)
//    val profileRef = Firebase.storage.reference.child("images/$uid.jpg")
//
//    var baseRequestOption = Glide.with(context).load(profileRef).placeholder(R.drawable.boxicon)
//
//    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()
//
//    if (useSignature) {
//        baseRequestOption = baseRequestOption.signature(
//            MediaStoreSignature("image/jpeg", version, 0)
//        )
//    }
//
//    baseRequestOption.into(imageView)
}

fun addImageWithMetadata(
    context: Context,
    uid: String, imageView: ImageView, textView: TextView,
    cropCircle: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val profileRef = Firebase.storage.reference.child("images/$uid.jpg")

    var baseRequestOption = Glide.with(context).load(profileRef).placeholder(R.drawable.boxicon)
    profileRef.metadata.addOnSuccessListener { metadata ->
        // Metadata now contains the metadata for 'images/forest.jpg'
        val nameMetadata = metadata.getCustomMetadata("name").toString()
        val stringBuilderName = StringBuilder(nameMetadata)
        if(nameMetadata.length>9){
            stringBuilderName.setCharAt(7, '.')
            stringBuilderName.setCharAt(8, '.')
            stringBuilderName.setCharAt(9, '.')

        }
        textView.text = stringBuilderName
    }.addOnFailureListener {
        // Uh-oh, an error occurred!
    }
    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()

    if (useSignature) {
        baseRequestOption = baseRequestOption.signature(
            MediaStoreSignature("image/jpeg", version, 0)
        )
    }

    baseRequestOption.into(imageView)
}

fun dispatchTakePictureIntent(activity: Activity) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    try {
        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    } catch (e: ActivityNotFoundException) {
        activity.longToast(
            "A problem occurred while opening your camera. " +
                    "Please make sure you have granted camera permissions to the app."
        )
    }
}

fun openImageFile(activity: Activity){
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
    }

    activity.startActivityForResult(intent, REQUEST_FILE)
}

fun rescaleImageView(bitmap: Bitmap, imageView: ImageView){
    val width = bitmap.width
    val height = bitmap.height

    val params = RelativeLayout.LayoutParams(width, height)
    imageView.layoutParams = params
    imageView.refreshDrawableState()
}

fun getMaxWidthAndHeight(context: Context): ArrayList<Double>{
    // code to calculate the phone width and height is taken from:
    // https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android#4744499
    // https://stackoverflow.com/questions/63276134/getter-for-defaultdisplay-display-is-deprecated-deprecated-in-java#63276263
    val displayMetrics = DisplayMetrics()
    context.display?.getRealMetrics(displayMetrics)
    val maxWidth = displayMetrics.widthPixels / 1.5
    val maxHeight = displayMetrics.heightPixels / 3.0

    return arrayListOf(maxWidth, maxHeight)
}

fun calculateParams(context: Context, imageWidth: Int, imageHeight: Int, maxWidth: Double, maxHeight: Double): ArrayList<Double>{
    val ratio = imageWidth.toDouble() / imageHeight.toDouble()
    val width: Double
    val height: Double

    if(ratio >= 1){
        width = maxWidth
        height = width / ratio
    }
    else{
        height = maxHeight
        width = height * ratio
    }

    return arrayListOf(width, height)
}

fun addImageAndResize(
    context: Context,
    path: String,
    imageView: ImageView,
    width: Double,
    height: Double,
    cropCircle: Boolean = true,
    roundCorners: Boolean = true,
    useSignature: Boolean = false,
    version: Long = 0
){
    val imageRef = Firebase.storage.reference.child(path)

    var baseRequestOption = Glide.with(context).load(imageRef).placeholder(R.drawable.boxicon)
        .fitCenter()
        .override(width.toInt(), height.toInt())

    if (cropCircle) baseRequestOption = baseRequestOption.circleCrop()

    if (roundCorners) {
        baseRequestOption = baseRequestOption
            .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
    }

    if (useSignature) {
        baseRequestOption = baseRequestOption.signature(
            MediaStoreSignature("image/jpeg", version, 0)
        )
    }

    baseRequestOption.into(imageView)
}