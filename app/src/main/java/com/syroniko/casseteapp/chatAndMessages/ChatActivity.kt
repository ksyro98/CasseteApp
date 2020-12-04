package com.syroniko.casseteapp.chatAndMessages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.profile.FullScreenImageActivity
import com.syroniko.casseteapp.profile.ProfileActivity
import com.syroniko.casseteapp.utils.addImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import javax.inject.Inject

const val FRIEND_ID = "Friend ID"

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val viewModel by viewModels<ChatViewModel>()
    @Inject lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        viewModel.displayedChat = intent.getParcelableExtra(CHAT_DETAILS_EXTRA_NAME) ?: return
        viewModel.fromNotification = intent.getBooleanExtra(FROM_NOTIFICATION_EXTRA_NAME, false)

        viewModel.startListeningToMessages()

        name_text_view.text = viewModel.displayedChat.userName
        addImage(this, viewModel.displayedChat.userId, profile_image_view)

        if (viewModel.messages.value != null){
            chatAdapter.messages = viewModel.messages.value!!
        }

        chat_activity_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        viewModel.messages.observe(this, androidx.lifecycle.Observer { messages ->
            chatAdapter.messages = messages
            chat_activity_recycler.scrollToPosition(chatAdapter.itemCount - 1)
        })

        chat_activity_send_message_button.setOnClickListener {
            val text = type_message_edit_text_chat_activity.text.toString()
            if (text == ""){
                return@setOnClickListener
            }

            sendMessage(viewModel.uid, viewModel.displayedChat.userId, text, lifecycleScope)

            type_message_edit_text_chat_activity.text.clear()
        }

//        KeyboardVisibilityEvent.setEventListener(
//            this,
//            object : KeyboardVisibilityEventListener {
//                override fun onVisibilityChanged(isOpen: Boolean) {
//                    if (isOpen) {
//                        chat_activity_recycler.scrollToPosition(chatAdapter.itemCount - 1)
//                    }
//                }
//            }
//        )


        info_linear_layout.setOnClickListener {
            ProfileActivity.startActivity(this, viewModel.displayedChat.userId, viewModel.uid)
        }

        profile_image_view.setOnClickListener {
            FullScreenImageActivity.startActivity(this, viewModel.displayedChat.userId)
        }

    }


    override fun onStop() {
        super.onStop()
        viewModel.stopListeningToMessages()
    }


    override fun onBackPressed() {
        if (viewModel.fromNotification){
            MainActivity.startActivity(this, viewModel.uid)
            finish()
        }
        else{
            super.onBackPressed()
        }
    }

    companion object {
        private const val CHAT_DETAILS_EXTRA_NAME = "chat details extra name"
        private const val FROM_NOTIFICATION_EXTRA_NAME = "from notification extra name"

        fun startActivity(context: Context, p: Parcelable, fromNotification: Boolean = false) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHAT_DETAILS_EXTRA_NAME, p)
            intent.putExtra(FROM_NOTIFICATION_EXTRA_NAME, fromNotification)
            context.startActivity(intent)
        }
    }
}