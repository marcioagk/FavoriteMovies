package com.example.android.favoritemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.favoritemovies.R;
import com.example.android.favoritemovies.objects.Movie;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MovieAdapter";

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private Movie[] mMovieData;
    private Context context;

    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        MoviesViewHolder holder = new MoviesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MoviesViewHolder holder = (MoviesViewHolder) viewHolder;
        String movieURL = String.format("%s%s", MOVIE_POSTER_BASE_URL, mMovieData[position].getPosterPath());
        Picasso.with(context).load(movieURL).into(holder.listItemMoviesImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieData == null ? 0 : mMovieData.length;
    }

    public void setMoviesData(Movie[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }


    public class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView listItemMoviesImageView;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            listItemMoviesImageView = (ImageView) itemView.findViewById(R.id.iv_item_movies);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickHandler.onClick(mMovieData[clickedPosition]);
        }
    }

}
