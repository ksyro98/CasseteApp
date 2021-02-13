package com.syroniko.casseteapp.chatAndMessages

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.chatAndMessages.entities.SpotifyTrackMessage
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.profile.FullScreenImageActivity
import com.syroniko.casseteapp.profile.PhotoPickerBottomSheetFragment
import com.syroniko.casseteapp.profile.ProfileActivity
import com.syroniko.casseteapp.utils.*
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

        info_linear_layout.setOnClickListener {
            ProfileActivity.startActivity(this, viewModel.displayedChat.userId, viewModel.uid)
        }

        profile_image_view.setOnClickListener {
            FullScreenImageActivity.startActivity(this, viewModel.displayedChat.userId)
        }

        back_button.setOnClickListener {
            onBackPressed()
        }

        chat_send_image_button.setOnClickListener {
            val bottomSheet = PhotoPickerBottomSheetFragment { which ->
                when (which) {
                    0 -> openImageFile(this)
                    1 -> dispatchTakePictureIntent(this)
                }
            }
            bottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startListeningToMessages()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = resultIntent?.extras?.get("data") as Bitmap

            sendImageMessage(viewModel.uid, viewModel.displayedChat.userId, imageBitmap)
        }
        else if (requestCode == REQUEST_FILE && resultCode == RESULT_OK){
            val uri = resultIntent?.data ?: return

            val pfd = contentResolver.openFileDescriptor(uri, "r")
            val fd = pfd?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fd)
            pfd?.close()

            sendImageMessage(viewModel.uid, viewModel.displayedChat.userId, bitmap)
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