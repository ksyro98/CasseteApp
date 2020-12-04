package com.syroniko.casseteapp

import android.R.id.text1
import android.R.id.text2
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.syroniko.casseteapp.mainClasses.USER_MAIN_EXTRA
import com.syroniko.casseteapp.mainClasses.User
import com.syroniko.casseteapp.profile.BioBottomSheetFragment


class CreateCassetteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cassette)
        var textOfEditText :String= " " ;
        var editTextOfSheet: (EditText)
        val user = intent.getParcelableExtra<User>(USER_MAIN_EXTRA)
        val searchSongButton: View =findViewById(R.id.pick_your_song_button_create_cassette)
        searchSongButton.setOnClickListener{
            val i = Intent(this, SearchSongActivity::class.java)
            i.putExtra(USER_MAIN_EXTRA, user)
            startActivity(i)
        }
        val tx = findViewById<TextView>(R.id.create_cassette_headline)
        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont
        val addMessageButton :View =findViewById(R.id.leave_a_message_button_create_cassette)
        addMessageButton.setOnClickListener{
//            val dialog=BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
//            val view=layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
//            dialog.setContentView(view)
//             editTextOfSheet =view.findViewById(R.id.leave_a_message_edit_text_bottom_sheet)
//            editTextOfSheet.setText(textOfEditText)
//            BottomSheetBehavior.from(view).addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    val text = editTextOfSheet.text.toString()
//                    textOfEditText=text
//                    Log.v("rempro","rempro:")
//
//                }
//
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                }
//            })
            val bottomSheet = NoteBottomSheetFragment()
            bottomSheet.show(this.supportFragmentManager, "ModalBottomSheet")
         //   dialog.show()
        }


    }

    companion object {
        fun startActivity(context: Context, user: User){
            val i = Intent(context, CreateCassetteActivity::class.java)
            i.putExtra(USER_MAIN_EXTRA, user)
            context.startActivity(i)
        }
    }
}