package com.syroniko.casseteapp.mainClasses

import android.content.Context
import android.util.Log
import com.syroniko.casseteapp.logInSignUp.GenreNameImageForSignupAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyLog.d
import com.android.volley.VolleyLog.v
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.logInSignUp.GenrePickSignupAdapter.GenreViewHolder
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.firebase.Auth
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.utils.addImage
import com.syroniko.casseteapp.utils.addImageWithMetadata
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.spotify_item.view.*

class StoryAdapter  internal constructor(
     private val context: Context,
    private val friendIds :ArrayList<String>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    init{
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StoryViewHolder {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.story_item, viewGroup, false)

        return StoryViewHolder(view)
    }

     override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
         Log.d("POUTZOS2","POUTZOS2")
         Log.d("POUTZOS3",friendIds.toString())
         addImageWithMetadata( context, friendIds[position], holder.imageView,holder.textView)
//         val story = storyList[position]
//        holder.textView.text = story.storyName

        holder.imageView.setOnClickListener {
            onImageClick(position, it as ImageView)
        }
    }

    override fun getItemCount() = friendIds.size

    private fun onImageClick(position: Int, imageView: ImageView){
   //     onClick(position)

    }

    class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.story_item_textview_profile_name)
        val imageView: ImageView = view.findViewById(R.id.story_item_imageview_profile_pic)
    }

}