package com.syroniko.casseteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = FirebaseFirestore.getInstance()

//        button.setOnClickListener{
//            val user = User(
//                senderIdEditText.text.toString(),
//                trackEditText.text.toString(),
//                commentEditText.text.toString(),
//                arrayListOf(genreEditText.text.toString()),
//                null,
//                arrayListOf())
//
//            database.collection("user").document("0").set(user)
//        }


        button.setOnClickListener {
            val cassette = Cassette(
                senderIdEditText.text.toString(),
                trackEditText.text.toString(),
                commentEditText.text.toString(),
                genreEditText.text.toString(),
                arrayListOf(),
                arrayListOf()
            )

            val primaryKey = senderIdEditText.text.toString() + "_" + Calendar.getInstance().time.time.toString()
            //double time is important, the first one prints the date and the second one the time in millisecond (from the Epoch)
            database.collection("cassette").document(primaryKey).set(cassette)
        }

    }
}
