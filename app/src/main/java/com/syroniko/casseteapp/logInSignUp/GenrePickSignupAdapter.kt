package com.syroniko.casseteapp.logInSignUp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.logInSignUp.GenrePickSignupAdapter.GenreViewHolder
import com.syroniko.casseteapp.R

class GenrePickSignupAdapter internal constructor(
    private val genreList: List<GenreNameImageForSignupAdapter>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<GenreViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GenreViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.genre_signup_item, viewGroup, false)

        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.textView.text = genre.genre

        holder.imageView.setImageResource(genre.genreImageDefault)
        holder.imageView.setOnClickListener {
            onImageClick(position, it as ImageView)
        }
    }

    override fun getItemCount() = genreList.size

    private fun onImageClick(position: Int, imageView: ImageView){
        onClick(position)

        genreList[position].isClicked = !genreList[position].isClicked

        val clicked = genreList[position].isClicked
        if (clicked) {
            imageView.setImageResource(genreList[position].genreImageClicked)
        }
        else {
            imageView.setImageResource(genreList[position].genreImageDefault)
        }
    }

    class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.genre_string_item)
        val imageView: ImageView = view.findViewById(R.id.genre_image_signup)
    }

}