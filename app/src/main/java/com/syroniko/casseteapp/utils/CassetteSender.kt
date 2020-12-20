package com.syroniko.casseteapp.utils

import android.util.Log
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
        val scaledInput = input / pow(10, 5)
        val scaledCurrentTime = System.currentTimeMillis() / pow(10, 5)
        val difference = scaledCurrentTime - scaledInput

        val returnValue = when {
            difference <= oneDay -> {
                Log.d("CassetteSender", "1")
                7500.0
            }
            difference <= oneWeek -> {
                Log.d("CassetteSender", "2")
                -0.675 * difference + 8083
            }
            difference <= oneMonth -> {
                Log.d("CassetteSender", "3")
                -0.148 * difference + 4896
            }
            else -> {
                Log.d("CassetteSender", "4")
                if (input == 0.toLong()){
                    0.toDouble()
                }
                else{
                    26298000 / (difference.toDouble())
                }
            }
        }

        return returnValue / pow(10, 4)
    }

}

