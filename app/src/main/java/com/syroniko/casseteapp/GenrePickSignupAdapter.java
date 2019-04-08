package com.syroniko.casseteapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenrePickSignupAdapter extends RecyclerView.Adapter<GenrePickSignupAdapter.GenreViewHolder> {
    private List<GenreNameImageForSignupAdapter> genreList;
     private GenreListClickListener genreListClickListener;



    public GenrePickSignupAdapter(Context context, List<GenreNameImageForSignupAdapter> genreList, GenreListClickListener genreListClickListener) {
        this.genreList = genreList;
        this.genreListClickListener=genreListClickListener;


    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.genre_signup_item,viewGroup,false );
        GenreViewHolder viewHolder = new GenreViewHolder(view);
        return viewHolder;
    }


    public interface GenreListClickListener{
        void OnListItemClick(int clickedItemPosition);

    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {

        GenreNameImageForSignupAdapter genre=genreList.get(position);
        holder.textView.setText(genre.getGenre());
        holder.imageView.setImageResource(genre.getGenreImageDefault());
    }

    @Override
    public int getItemCount() {

        return genreList.size();

    }

    class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView textView;
        private ImageView imageView;
        public GenreViewHolder(View view){
            super(view);
            textView=(TextView) view.findViewById(R.id.genre_string_item);
            imageView=view.findViewById(R.id.genre_image_signup);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition=getAdapterPosition();
           genreListClickListener.OnListItemClick(clickedPosition);
           genreList.get(clickedPosition).setClicked(!genreList.get(clickedPosition).getClicked());
           Boolean state=genreList.get(clickedPosition).getClicked();
           if (state==false) {
               imageView.setImageResource(genreList.get(clickedPosition).getGenreImageDefault());
           }
           else{
               imageView.setImageResource(genreList.get(clickedPosition).getGenreImageClicked());           }
        }

    }
}
