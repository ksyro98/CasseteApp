package com.syroniko.casseteapp.mainClasses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.*
import com.syroniko.casseteapp.chatAndMessages.MessagesFragment
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.profile.ProfileFragment
import com.syroniko.casseteapp.utils.*
import com.syroniko.casseteapp.viewCassette.CASSETTE_VIEWER_REQUEST_CODE
import com.syroniko.casseteapp.viewCassette.RESULT_FORWARD
import com.syroniko.casseteapp.viewCassette.RESULT_RESPONSE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService


const val spotifyQueryExtraName = "Spotify Query Extra Name"
const val USER_EXTRA_NAME = "User Extra Name"
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
        else{
            UserDB.getDocumentFromId(viewModel.uid)
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if(user == null) {
                        toast("An unexpected error occurred. Please try entering the app again.")
                        finish()
                    }
                    else{
                        viewModel.user = user
                    }
                }
        }

        val fab: View=findViewById(R.id.mainFab)
        fab.setOnClickListener{
            CreateCassetteActivity.startActivity(this, viewModel.user)
        }

        //TODO delete this, this is a temporary solution
        fab.setOnLongClickListener {
            selectedFragment = CassetteCaseFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, selectedFragment).commit()
            return@setOnLongClickListener true
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

        if(!SpotifyAuthRequest.isAuthorized(this) && !hasDeniedSpotifyConnection(this)){
            SpotifyDialogFragment().show(supportFragmentManager, SPOTIFY_FRAGMENT_TAG)
        }

        if (isFirstTime(this)){
            markFirstTime(this)
        }
        //
        //
        //
        //
        //




        //
        //
        //
        //
        //
        //
        //
    }

    override fun onStart() {
        super.onStart()
        viewModel.startListeningToChats()
        viewModel.setUserOnline()
    }

    override fun onStop() {
        super.onStop()
        viewModel.setUserOffline()
        viewModel.stopListeningToChats()
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
        else if(requestCode == AUTH_REQUEST_CODE && data != null){
            val response = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)

            if (response != null){
                SpotifyAuthRequest.updateAuth(this, response, ex)

                val authService = AuthorizationService(this)

                authService.performTokenRequest(response.createTokenExchangeRequest()){ resp, ex2 ->
                    if (resp != null) {
                        SpotifyAuthRequest.updateAuth(this, resp, ex2)
                    }
                }

            }

            if(ex != null){
                Log.e(MainActivity::class.java.simpleName, "Auth exception", ex)
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
