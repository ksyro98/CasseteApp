package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.syroniko.casseteapp.CassetteCaseFragment
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.CreateCassetteActivity
import com.syroniko.casseteapp.cassetteIdExtraName
import com.syroniko.casseteapp.viewCassette.CASSETTE_VIEWER_REQUEST_CODE
import com.syroniko.casseteapp.viewCassette.RESULT_FORWARD
import com.syroniko.casseteapp.viewCassette.RESULT_RESPONSE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val TOKEN_EXTRA_NAME = "Token Extra Name"

const val clientId = "846a7d470725449994155b664cb7959b"
const val redirectUri = "https://duckduckgo.com"

const val UID_MAIN_EXTRA = "uid main extra"
const val USER_MAIN_EXTRA = "user main extra"
const val TOKEN_MAIN_EXTRA = "token main extra"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var selectedFragment: Fragment
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab :View=findViewById(R.id.mainFab)
        fab.setOnClickListener{
            CreateCassetteActivity.startActivity(this)
        }

        if(intent.hasExtra(USER_MAIN_EXTRA) && intent.getParcelableExtra<User>(USER_MAIN_EXTRA) != null){
            viewModel.user = intent.getParcelableExtra(USER_MAIN_EXTRA)!!
        }
        if (intent.hasExtra(TOKEN_MAIN_EXTRA) && intent.getStringExtra(TOKEN_MAIN_EXTRA) != null) {
            viewModel.token = intent.getStringExtra(TOKEN_MAIN_EXTRA)!!
        }

        selectedFragment = CassetteCaseFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, selectedFragment).commit()

        chatBottomNavigationIcon.setOnClickListener {
            selectedFragment = MessagesFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, selectedFragment).commit()
        }

        viewModel.startListeningToChats()
        viewModel.setUserOnline()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListeningToChats()
        viewModel.setUserOffline()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CASSETTE_VIEWER_REQUEST_CODE && data != null){
            if (resultCode == RESULT_RESPONSE || resultCode == RESULT_FORWARD){
                val cassetteId = data.getStringExtra(cassetteIdExtraName) ?: return
                viewModel.removeCassette(cassetteId)
            }
        }
    }

    companion object{
        fun startActivity(context: Context, uid: String?, user: User? = null, token: String? = null){
            val i = Intent(context, MainActivity::class.java)

            i.putExtra(UID_MAIN_EXTRA, uid)
            i.putExtra(USER_MAIN_EXTRA, user)
            i.putExtra(TOKEN_MAIN_EXTRA, token)

            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            context.startActivity(i)
        }
    }

}

fun Context.toast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
