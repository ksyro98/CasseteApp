package com.syroniko.casseteapp.LogInSignUp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.User
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.utils.SPOTIFY_NO_TOKEN
import java.util.*

class PickGenresSignUpActivity : AppCompatActivity() {

    private val list = GENRES_LIST
    private val genreList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_genres_sign_up)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_signup_genres)
        val next = findViewById<TextView>(R.id.nextfromgenres_to)
        val tx = findViewById<TextView>(R.id.pickthegenresyouenjou_string)

        val customFont = Typeface.createFromAsset(assets, "fonts/montsextrathic.ttf")
        tx.typeface = customFont


        val user = intent.getParcelableExtra<User>(GENRES_USER_EXTRA) ?: return
        val password = intent.getStringExtra(GENRES_PASSWORD_EXTRA) ?: return
        val spotifyToken = intent.getStringExtra(SPOTIFY_TOKEN_EXTRA_NAME) ?: SPOTIFY_NO_TOKEN

        val genreAdapter = GenrePickSignupAdapter(list) { clickedItemPosition ->
            if (!list[clickedItemPosition].isClicked) {
                genreList.add(list[clickedItemPosition].genre)
            }
            else {
                genreList.remove(list[clickedItemPosition].genre)
            }
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            setHasFixedSize(true)
            adapter = genreAdapter
            isFocusable = false
        }

        ViewCompat.setNestedScrollingEnabled(recyclerView, false)

        next.setOnClickListener {
            if (genreList.size == 0) {
                toast("Please select at least one genre.")
            }
            else {
                user.genres = genreList
                CountrySelectSignUpActivity.startActivity(this, user, password, spotifyToken)
                finish()
            }
        }
    }

    companion object {
        private const val GENRES_USER_EXTRA = "genres user extra"
        private const val GENRES_PASSWORD_EXTRA = "genres password extra"
        private const val SPOTIFY_TOKEN_EXTRA_NAME = "spotify token extra name"


        fun startActivity(context: Context, user: User, password: String, spotifyToken: String){
            val intent = Intent(context, PickGenresSignUpActivity::class.java)
            intent.putExtra(GENRES_USER_EXTRA, user)
            intent.putExtra(GENRES_PASSWORD_EXTRA, password)
            intent.putExtra(SPOTIFY_TOKEN_EXTRA_NAME, spotifyToken)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}