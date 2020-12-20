package com.syroniko.casseteapp.utils

import com.google.common.math.IntMath.pow
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.chatAndMessages.getTime
import com.syroniko.casseteapp.firebase.CassetteDB
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.Cassette
import com.syroniko.casseteapp.mainClasses.User
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random


class CassetteSender(
    private val cassette: Cassette,
    private val scope: CoroutineScope,
    private val callback: () -> Unit
) {

    private val oneDay = 86400000 / pow(10, 5)
    private val oneWeek = 604800000 / pow(10, 5)
    private val oneMonth = 2629800000 / pow(10, 5)

    fun startCassetteSendingAlgorithm() {
        UserDB.getUsersForPossibleReceivers(cassette.genre ?: "pop", cassette.restrictedReceivers) { possibleReceivers ->
            findReceiverAndSend(possibleReceivers)
        }
    }

    private fun findReceiverAndSend(
        possibleReceivers: ArrayList<User>
    ) {
        for (user in possibleReceivers) {
            val r = Random.nextDouble()
            if (f(user.lastOnline) >= r) {
                sendCassette(user.uid)
                return
            }
        }
        sendCassette(possibleReceivers[0].uid)
    }

    private fun sendCassette(receiverId: String?) {
        if (receiverId == null){
            return
        }

        cassette.receiver = receiverId
        cassette.received = true
        CassetteDB.insertWithCallback(cassette) { id ->
            getTime(scope) { time ->
                UserDB.update(receiverId, hashMapOf(
                    Pair("cassettes", FieldValue.arrayUnion(id)),
                    Pair("receivedLastCassetteAt", time)
                ))
            }
            callback()
        }
    }

    private fun f(input: Long): Double {
        var returnValue = 0.0

        returnValue = when {
            input <= oneDay -> {
                7500.0
            }
            input <= oneWeek -> {
                -0.675 * input + 8083
            }
            input <= oneMonth -> {
                -0.148 * input + 4896
            }
            else -> {
                26298000 / (input.toDouble())
            }
        }

        return returnValue / pow(10, 4)
    }

}

