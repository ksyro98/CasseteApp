package com.syroniko.casseteapp.MainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.ChatAndMessages.MessagesFragment
import com.syroniko.casseteapp.viewCassette.CASSETTE_VIEWER_REQUEST_CODE
import com.syroniko.casseteapp.viewCassette.RESULT_FORWARD
import com.syroniko.casseteapp.viewCassette.RESULT_RESPONSE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val USER_EXTRA_NAME = "User Extra Name"

const val clientId = "846a7d470725449994155b664cb7959b"
const val redirectUri = "https://duckduckgo.com"

const val UID_MAIN_EXTRA = "uid main extra"
const val USER_MAIN_EXTRA = "user main extra"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var selectedFragment: Fragment
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.hasExtra(USER_MAIN_EXTRA) && intent.getParcelableExtra<User>(USER_MAIN_EXTRA) != null){
            viewModel.user = intent.getParcelableExtra(USER_MAIN_EXTRA)!!
        }


        val fab: View=findViewById(R.id.mainFab)
        fab.setOnClickListener{
            CreateCassetteActivity.startActivity(this, viewModel.user)
        }


        selectedFragment = CassetteCaseFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, selectedFragment).commit()

        chatBottomNavigationIcon.setOnClickListener {
            selectedFragment = MessagesFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, selectedFragment).commit()
        }

        profileBottomNavigationIcon.setOnClickListener {
            selectedFragment = ProfileFragment()
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
        else if (requestCode == CASSETTE_VIEWER_REQUEST_CODE && data != null){
            if(resultCode == RESULT_RESPONSE){
                val cassetteId = data.getStringExtra(cassetteIdExtraName)
                val senderId = data.getStringExtra(userIdExtraName)

                viewModel.updateUserOnCassetteAction(senderId, cassetteId, true)
            }
            else if (resultCode == RESULT_FORWARD){
                val cassetteId = data.getStringExtra(cassetteIdExtraName)

                viewModel.updateUserOnCassetteAction(null, cassetteId, false)
            }
        }
    }

    companion object{
        fun startActivity(context: Context, uid: String?, user: User? = null){
            val i = Intent(context, MainActivity::class.java)

            i.putExtra(UID_MAIN_EXTRA, uid)
            if(user != null) {
                i.putExtra(USER_MAIN_EXTRA, user)
            }
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            context.startActivity(i)
        }
    }

}

fun Context.toast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
