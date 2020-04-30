package com.syroniko.casseteapp.ChatAndMessages;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.syroniko.casseteapp.MainClasses.User;
import com.syroniko.casseteapp.R;

import java.util.List;

public class FriendChatListAdapter extends RecyclerView.Adapter<FriendChatListAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private boolean isOnline;

    public FriendChatListAdapter (Context mContext,List<User> mUsers){
        this.mUsers=mUsers;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.friend_list_recycler_item,parent,false);
        return new FriendChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user=mUsers.get(position);
        holder.userName.setText(user.getName());
        if(user.getImage()==null){
            holder.userImage.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImage()).placeholder(R.drawable.greenbox).into(holder.userImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,ChatActivity.class);
                i.putExtra("FRIENDUSERID",user.getUid());
                i.putExtra("FRIENDUSERNAME",user.getName());
                mContext.startActivity(i);
            }
        });
        if(user.getStatus().equals("online")){
         //   Log.v("KALO",user.getName());
            holder.isAvailableGreenCircle.setVisibility(View.VISIBLE);
        }
        if(user.getStatus().equals("offline")){

            holder.isAvailableGreenCircle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public ImageView userImage;
        public ImageView isAvailableGreenCircle;


        public ViewHolder(View itemView){
            super(itemView);

            userName=itemView.findViewById(R.id.username_friend_list_item_message_fragment_recycler);
            userImage=itemView.findViewById(R.id.message_fragment_recycler_item_user_image);
            isAvailableGreenCircle=itemView.findViewById(R.id.is_online_on_green_circle_image);

        }

    }
}
