package com.syroniko.casseteapp.ChatAndMessages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.syroniko.casseteapp.MainClasses.toast
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.ChatDB
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

const val FRIEND_ID = "Friend ID"

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    //    private var messageEditText: EditText? = null
//    private var sendButton: ImageButton? = null
//    private var infoLinearLayout: LinearLayout? = null
//    private var profileImageView: ImageView? = null
//    private var nameTextView: TextView? = null
    private var db: FirebaseFirestore? = null
    private var friendChattingId: String? = null
    private var friendName: String? = null
    private var mChat: List<Chat>? = null
    private var docRef: DocumentReference? = null
    private var colref: CollectionReference? = null
    private val seenListener: ListenerRegistration? = null
    //    private var recyclerView: RecyclerView? = null


    @Inject lateinit var chatAdapter: ChatAdapter
    private val uid: String? = Auth.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        messageEditText = findViewById(R.id.type_message_edit_text_chat_activity)
//        sendButton = findViewById(R.id.chat_activity_send_message_button)
//        infoLinearLayout = findViewById(R.id.info_linear_layout)
//        profileImageView = findViewById(R.id.profile_image_view)
//        nameTextView = findViewById(R.id.name_text_view)

        if (uid == null){
            return
        }

        val displayedChat = intent.getParcelableExtra<DisplayedChat>(CHAT_DETAILS_EXTRA_NAME) ?: return

        name_text_view.text = displayedChat.userName

        chat_activity_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }


//        chatDB.getDocumentFromId(displayedChat.chatId)
//            .addOnSuccessListener { documentSnapshot ->
//                val chat = documentSnapshot.toObject(Chat::class.java) ?: return@addOnSuccessListener
//                chatAdapter.messages = chat.messages
//            }

        ChatDB.listenToMessages(displayedChat.chatId) { document ->
            val chat = document.toObject(Chat::class.java) ?: return@listenToMessages
            chat.messages.map { message -> if(message.senderId != uid) message.read = true }
            ChatDB.update(displayedChat.chatId, hashMapOf(Pair("messages", chat.messages)))
            chatAdapter.messages = chat.messages
            chat_activity_recycler.scrollToPosition(chatAdapter.itemCount - 1)
        }

        chat_activity_send_message_button.setOnClickListener {
            val text = type_message_edit_text_chat_activity.text.toString()
            if (text == ""){
                return@setOnClickListener
            }

            sendMessage(uid, displayedChat.userId, text)

            type_message_edit_text_chat_activity.text.clear()
        }

        KeyboardVisibilityEvent.setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        chat_activity_recycler.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        )


//        db = FirebaseFirestore.getInstance()
//        db!!.collection("users")
//            .document(friendChattingId!!)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                val name = documentSnapshot.getString("name")
//                nameTextView.setText(name)
//                val imageUrl = documentSnapshot.getString("image")
//                Glide.with(applicationContext).load(imageUrl)
//                    .placeholder(R.drawable.greenbox).into(profileImageView)
//            }
//        val activity: Activity = this
//        sendButton.setOnClickListener(View.OnClickListener {
//            if (messageEditText.getText().toString() != "") {
//                sendMessage(
//                    activity,
//                    uid!!,
//                    friendChattingId!!,
//                    messageEditText.getText().toString(),
//                    System.currentTimeMillis().toString(),
//                    db!!
//                )
//            }
//        })
//        docRef = db!!.collection("users").document(uid!!)
//        docRef!!.addSnapshotListener(EventListener { snapshot, e ->
//            if (e != null) {
//                return@EventListener
//            }
//            if (snapshot != null && snapshot.exists()) {
//                readMessages(uid!!, friendChattingId, "default")
//            }
//        })
//        infoLinearLayout.setOnClickListener(View.OnClickListener {
//            val profileIntent =
//                Intent(applicationContext, ProfileActivity::class.java)
//            profileIntent.putExtra(FRIEND_ID, friendChattingId)
//            startActivity(profileIntent)
//        })


    }



    /*private void seenMessage(final String userId){
        colref = db.collection("chats");
        seenListener = colref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                Log.v("AKA",String.valueOf(value.getDocuments().size()));
                if (e != null) {
                    return;
                }

              //  mChat.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Chat chat = doc.toObject(Chat.class);
                    if (chat.getReceiver().equals(uid) && chat.getSender().equals(friendChattingId)) {
                        Log.d("HAHA",doc.getString("receiver")+" and "+uid);
                        Log.d("HAHA",doc.getString("sender")+" and "+friendChattingId);

                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        doc.getReference().update(hashMap);//edo isos ta kanei ola mazi true, na koitakseis

                    }


                }
            }
        });

    }*/
    //    private void sendMessage(String sender,String receiver,String time){
    //        Map<String, Object> user = new HashMap<>();
    //        if(sender.hashCode()>receiver.hashCode()) {
    //            user.put("chatid", sender + receiver);
    //        }
    //        else{
    //            user.put("chatid", receiver + sender);
    //
    //        }
    //
    //        user.put("receiver", receiver);
    //        user.put("sender",sender);
    //        user.put("isseen",false);
    //        user.put("timestamp", time);
    //
    //
    //        user.put("message", messageEditText.getText().toString());
    //
    //
    //        db.collection("chats")
    //                .add(user)
    //                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
    //                    @Override
    //                    public void onSuccess(DocumentReference documentReference) {
    //                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
    //                    }
    //                })
    //                .addOnFailureListener(new OnFailureListener() {
    //                    @Override
    //                    public void onFailure(@NonNull Exception e) {
    //                        Log.w("TAG", "Error adding document", e);
    //                    }
    //                });
    //    }
    private fun readMessages(
        myId: String,
        userId: String?,
        imageUrl: String
    ) {
        mChat = ArrayList()
        val path: String
        path = if (myId.hashCode() > userId.hashCode()) {
            myId + userId
        } else {
            userId + myId
        }
        colref = db!!.collection("chats")
        colref!!.orderBy("timestamp") //.whereEqualTo("chatid", path)
            .addSnapshotListener { value, e ->
                //                        Log.d("HAHA", "uid is"+ " in  read " + uid);
//                        if (e != null) {
//                            return;
//                        }
//
//                        mChat.clear();
//                        for (QueryDocumentSnapshot doc : value) {
//                            Chat chat=doc.toObject(Chat.class);
//
//                            Log.d("cmo",String.valueOf(doc.getBoolean("isseen")));
//                            chat.setSeen(doc.getBoolean("isseen"));
//                            chat.timestamp = (doc.getString("timestamp"));
//
//                            if(chat.receiver.equals(myId) && chat.sender.equals(userId) ||
//                                    (chat.receiver.equals(userId) && chat.sender.equals(myId))){
//                                mChat.add(chat);
//                            }
//
//
//                            //TODO why is this in the loop??
//                            adapter=new ChatAdapter(ChatActivity.this,mChat,imageUrl);
//                            recyclerView.setAdapter(adapter);
//
//                            if(!doc.getBoolean("isseen")) {
//
//
//                                if (chat.receiver.equals(uid) && chat.sender.equals(friendChattingId)) {
//
//                                    Map<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put("isseen", true);
//                                    doc.getReference().update(hashMap);//edo isos ta kanei ola mazi true, na koitakseis
//                                }
//                            }
//
//                        }
            }
    }

    fun status(status: String) {
        val data: MutableMap<String, Any> = HashMap()
        data["status"] = status
        db = FirebaseFirestore.getInstance()
        db!!.collection("users").document(uid!!)[data] = SetOptions.merge()
    }

    override fun onResume() {
        super.onResume()
//        status("online")
    }

    override fun onPause() {
        super.onPause()
//        status("offline")
    }

    override fun onStop() {
        super.onStop()
        ChatDB.detachListener()
    }

    companion object {
        private const val CHAT_DETAILS_EXTRA_NAME = "chat details extra name"

        fun startActivity(context: Context, p: Parcelable) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHAT_DETAILS_EXTRA_NAME, p)
            context.startActivity(intent)
        }
    }
}