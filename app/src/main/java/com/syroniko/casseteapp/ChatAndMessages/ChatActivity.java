package com.syroniko.casseteapp.ChatAndMessages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.syroniko.casseteapp.MainClasses.CoreActivity;
import com.syroniko.casseteapp.MainClasses.User;
import com.syroniko.casseteapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static com.syroniko.casseteapp.ChatAndMessages.ChatUtilsKt.sendMessage;

public class ChatActivity extends AppCompatActivity {
    EditText messageEditText;
    ImageButton sendButton;
    FirebaseFirestore db;
    String friendChattingId;
    String uid;
    String friendName;
    List<Chat> mChat;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    DocumentReference docRef;
    CollectionReference colref;
    ListenerRegistration seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView=findViewById(R.id.chat_activity_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageEditText=findViewById(R.id.type_message_edit_text_chat_activity);
        sendButton=findViewById(R.id.chat_activity_send_message_button);
        Intent intent=getIntent();
         friendChattingId=intent.getStringExtra("FRIENDUSERID");
      //   uid= CoreActivity.uid;
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("HAHA", "uid is"+ " in anathesi " + uid);

        friendName=intent.getStringExtra("FRIENDUSERNAME");
        db= FirebaseFirestore.getInstance();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageEditText.getText().toString().equals("")) {
                    sendMessage(
                            uid,
                            friendChattingId,
                            messageEditText.getText().toString(),
                            String.valueOf(System.currentTimeMillis()),
                            db);
                }
            }
        });
         docRef = db.collection("users").document(uid);
         docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot snapshot,
                                 @Nullable FirebaseFirestoreException e) {

                 if (e != null) {
                     return;
                 }

                 if (snapshot != null && snapshot.exists()) {

                     readMessages(uid,friendChattingId,"default");
                 } else {
                 }
             }
         });
   //      seenMessage(uid);
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

    private void readMessages(final String myId, final String userId, final String imageUrl ){
        mChat=new ArrayList<>();
        String path;
        if(myId.hashCode()>userId.hashCode()) {


           path= myId + userId;
        }
        else{
            path= userId + myId;



        }
        colref= db.collection("chats");
        colref.orderBy("timestamp")//.whereEqualTo("chatid", path)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        Log.d("HAHA", "uid is"+ " in  read " + uid);
                        if (e != null) {
                            return;
                        }

                        mChat.clear();
                        for (QueryDocumentSnapshot doc : value) {
                           Chat chat=doc.toObject(Chat.class);
                           Log.d("cmo",String.valueOf(doc.getBoolean("isseen")));
                           chat.setSeen(doc.getBoolean("isseen"));
                           chat.setTimestamp((doc.getString("timestamp")));
                           if(chat.getReceiver().equals(myId)&&chat.getSender().equals(userId)||
                           (chat.getReceiver().equals(userId)&&chat.getSender().equals(myId))){
                               mChat.add(chat);

                            }
                            adapter=new ChatAdapter(ChatActivity.this,mChat,imageUrl);
                           recyclerView.setAdapter(adapter);

                            if(!doc.getBoolean("isseen")) {


                                if (chat.getReceiver().equals(uid) && chat.getSender().equals(friendChattingId)) {

                                    Map<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("isseen", true);
                                    doc.getReference().update(hashMap);//edo isos ta kanei ola mazi true, na koitakseis
                                }
                            }

                        }
                    }
                });



    }
    void status(String status ){

        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        db= FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .set(data, SetOptions.merge());
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
