package com.syroniko.casseteapp.utils

import androidx.lifecycle.MutableLiveData


fun <T> MutableLiveData<MutableList<T>>.addAndUpdate(item: T){
    val tempValue = this.value
    tempValue?.add(item)
    this.value = tempValue
}
