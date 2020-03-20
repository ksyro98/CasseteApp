package com.syroniko.casseteapp.ChatAndMessages;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.syroniko.casseteapp.MainClasses.User;
import com.syroniko.casseteapp.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context mContext;
    public static final int MESSAGE_RIGHT=0;
    public static final int MESSAGE_LEFT=1;
    private List<Chat> mChat;
    private String imageUrl;
    FirebaseUser fUser;

    public ChatAdapter (Context mContext,List<Chat> mChat,String ImageUrl){
        this.mChat=mChat;
        this.mContext=mContext;
        this.imageUrl= ImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MESSAGE_RIGHT){
            Log.d("SASA","SUSA");
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new ChatAdapter.ViewHolder(view);
        }
        else {
            Log.d("Sasa","Sisa");
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat =mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.timestampTv.setVisibility(View.INVISIBLE);
        if(imageUrl=="default"){
         //   holder.userImage.setImageResource(R.mipmap.ic_launcher);
        }
        else{
      //      Glide.with(mContext).load(imageUrl).into(holder.userImage);
        }
        if(position==mChat.size()-1){
            Log.d("cmoon",String.valueOf(chat.isSeen()));
            if(chat.isSeen()){
                holder.messageSeen.setText("Seen");
            }
            else{
                holder.messageSeen.setText("Delivered");


            }
        }
        else{
            holder.messageSeen.setVisibility(View.INVISIBLE);
        }
        String time=chat.getTimestamp();
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String dateTime= DateFormat.format("hh:mm",cal).toString();
        holder.timestampTv.setText(dateTime);

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView show_message;
        public ImageView userImage;
        public TextView messageSeen;
        public TextView timestampTv;

        public ViewHolder(View itemView){
            super(itemView);
            show_message=itemView.findViewById(R.id.chat_item_textView_bubble);
            userImage=itemView.findViewById(R.id.message_fragment_recycler_item_user_image);
            messageSeen=itemView.findViewById(R.id.message_seen_text);
            timestampTv=itemView.findViewById(R.id.timestamptextviewchat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.v("gottem","gorrwm");
            if(timestampTv.getVisibility()==View.INVISIBLE) {
                timestampTv.setVisibility(View.VISIBLE);
            }
            else{
                timestampTv.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fUser.getUid())){
            return MESSAGE_RIGHT;
        }
        else{
            return MESSAGE_LEFT;
        }
    }
}
