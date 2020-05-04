package com.syroniko.casseteapp.utils

import android.util.Log
import com.google.android.gms.common.util.ArrayUtils
import com.syroniko.casseteapp.MainClasses.User

fun <T> removeDuplicates(list: List<T>): List<T>{
    Log.d(ArrayUtils::class.java.simpleName, list.toString())
    Log.d(ArrayUtils::class.java.simpleName, list.distinct().toString())
    return list.distinct()
}


fun sortUserList(list: ArrayList<UserAndTime>): ArrayList<UserAndTime>{
    val temp = list.sortedWith(compareBy { -it.time })
    val res = arrayListOf<UserAndTime>()
    res.addAll(temp)
    return res
}
