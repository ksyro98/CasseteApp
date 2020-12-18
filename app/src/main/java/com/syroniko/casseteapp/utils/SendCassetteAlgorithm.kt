package com.syroniko.casseteapp.utils

import com.google.common.math.IntMath.pow
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.Cassette
import com.syroniko.casseteapp.mainClasses.User
import kotlin.random.Random

private val one_day = 86400000 / pow(10, 5)
private val one_week = 604800000 / pow(10, 5)
private val one_month = 2629800000 / pow(10, 5)

private fun f(input: Long): Double {
    var returnValue = 0.0

    returnValue = when {
        input <= one_day -> {
            7500.0
        }
        input <= one_week -> {
            -0.675 * input + 8083
        }
        input <= one_month -> {
            -0.148 * input + 4896
        }
        else -> {
            26298000 / (input.toDouble())
        }
    }

    return returnValue / pow(10, 4)
}

private fun findReceiverAndSend(cassette: Cassette, possibleReceivers: ArrayList<User>, callback: () -> Unit){
    for (user in possibleReceivers){
        val r = Random.nextDouble()
        if (f(user.lastOnline) >= r){
            sendCassette(cassette, user.uid ?: "", callback)
            return
        }
    }
    sendCassette(cassette, possibleReceivers[0].uid ?: "", callback)
}

private fun sendCassette(cassette: Cassette, receiverId: String, callback: () -> Unit){
    cassette.receiver = receiverId
    CassetteDB.insert(cassette)
    callback()
}

fun startCassetteSendingAlgorithm(cassette: Cassette, callback: () -> Unit){
    UserDB.getUsersForPossibleReceivers(cassette.genre ?: "pop", cassette.restrictedReceivers) {
        findReceiverAndSend(cassette, it, callback)
    }
}

