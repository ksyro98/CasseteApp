package com.syroniko.casseteapp.LogInSignUp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.syroniko.casseteapp.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenrePickSignupAdapter extends RecyclerView.Adapter<GenrePickSignupAdapter.GenreViewHolder> {

    private List<GenreNameImageForSignupAdapter> genreList;
    private GenreListClickListener genreListClickListener;


    GenrePickSignupAdapter(List<GenreNameImageForSignupAdapter> genreList, GenreListClickListener genreListClickListener) {
        this.genreList = genreList;
        this.genreListClickListener=genreListClickListener;
    }

    @NotNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.genre_signup_item,viewGroup,false );
        return new GenreViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NotNull GenreViewHolder holder, int position) {
        GenreNameImageForSignupAdapter genre = genreList.get(position);
        holder.textView.setText(genre.getGenre());
        holder.imageView.setImageResource(genre.getGenreImageDefault());
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public interface GenreListClickListener{
        void OnListItemClick(int clickedItemPosition);
    }

    class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        private ImageView imageView;

        GenreViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.genre_string_item);
            imageView = view.findViewById(R.id.genre_image_signup);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();

            genreListClickListener.OnListItemClick(clickedPosition);
            genreList.get(clickedPosition).setClicked(!genreList.get(clickedPosition).isClicked());

            Boolean state = genreList.get(clickedPosition).isClicked();

            if (!state) {
                imageView.setImageResource(genreList.get(clickedPosition).getGenreImageDefault());
            }
            else{
                imageView.setImageResource(genreList.get(clickedPosition).getGenreImageClicked());
            }
        }

    }
}
