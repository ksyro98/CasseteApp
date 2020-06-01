package com.syroniko.casseteapp.ChatAndMessages;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.syroniko.casseteapp.MainClasses.MainActivity;
import com.syroniko.casseteapp.MainClasses.User;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.room.UserAndTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.syroniko.casseteapp.utils.ArrayUtilsKt.sortUserList;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private static ArrayList<UserAndTime> list;
    private FriendChatListAdapter adapter;
    private EditText searchEditText;
    private String uid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_messages,container,false);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Fragment fragment = this;

        recyclerView=view.findViewById(R.id.recycler_of_friends_message_fragment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchEditText=view.findViewById(R.id.search_users_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        list=new ArrayList<>();
        uid = ((MainActivity) getActivity()).getUid();

        DocumentReference docRef = db.collection("users").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                       final HashMap<String, Long> friends= (HashMap<String, Long>) document.get("friends");

//                       for(int i =0;i<friends.size();i++){
                        for (final Map.Entry<String, Long> entry : friends.entrySet()){
                           DocumentReference friendRef = db.collection("users").document(entry.getKey());
                           friendRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if (task.isSuccessful()) {
                                       DocumentSnapshot friendDocument = task.getResult();

                                       if (friendDocument.exists()) {
                                           User newUser = friendDocument.toObject(User.class);

                                           list.add(new UserAndTime(newUser, entry.getValue()));
                                           list = sortUserList(list);
                                           Log.v("zazaza", String.valueOf(list.size()));
                                       }
                                   }

                                   adapter = new FriendChatListAdapter(getContext(), list);
                                   recyclerView.setAdapter(adapter);
                                   adapter.notifyDataSetChanged();
                               }
                           });
                        }
                        ChatUtilsKt.insertInLocalDb(fragment, list);

                    } else {
                        Log.d("TAG", "No such document");
                    }


                } else {
                    Log.d("TAG", "get failed with ", task.getException());

                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapter == null || list == null){
            return;
        }
        sortUserList(list);
        Log.d(MessagesFragment.class.getSimpleName(), list.toString());
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "AHA", Toast.LENGTH_SHORT).show();
    }

    private void searchUsers(String s) {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference colRef = db.collection("users");
        colRef.orderBy("name").startAt(s).endAt(s+"\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot value,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Log.v("AKA",String.valueOf(value.getDocuments().size()));

                        if (e != null) {
                            return;
                        }


                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String name=  doc.getString("name");
                            name=name.toLowerCase();
                            String isOnline=doc.getString("status");
                            User user=new User(name,doc.getString("email"),isOnline,doc.getLong("receivedLastCassetteAt").longValue(),null,doc.getString("uid"),doc.getString("country"),
                                    new ArrayList<String>(),new HashMap<String, Long>(), new ArrayList<String>(), 0, 0, new ArrayList<String>(), "", new ArrayList<String>(), new ArrayList<String>());
                            if(!user.getUid().equals(firebaseUser.getUid())){
                                list.add(new UserAndTime(user, 0));
                            }
                        }
                        adapter=new FriendChatListAdapter(getContext(),list);
                        recyclerView.setAdapter(adapter);
                    }
                });
        }


    }

