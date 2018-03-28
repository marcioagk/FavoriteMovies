package com.example.android.favoritemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.favoritemovies.R;
import com.example.android.favoritemovies.holders.MovieReviewViewHolder;
import com.example.android.favoritemovies.objects.Review;

/**
 * Created by lsitec26.kanashiro on 15/03/18.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MovieReviewAdapter";

    private Review[] mMovieReviews;
    private Context context;

    public MovieReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "DEBUG - MovieReviewAdapter - onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        MovieReviewViewHolder holder = new MovieReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Log.d(TAG, "DEBUG - MovieReviewAdapter - onBindViewHolder");
        MovieReviewViewHolder holder = (MovieReviewViewHolder) viewHolder;
        holder.mReviewAuthorTextView.setText(String.format(context.getString(R.string.written_by), mMovieReviews[position].getAuthor()));
        holder.mReviewContentTextView.setText(mMovieReviews[position].getContent());
    }

    @Override
    public int getItemCount() {
        return mMovieReviews == null ? 0 : mMovieReviews.length;
    }

    public void setReviews(Review[] movieReviews) {
        mMovieReviews = movieReviews;
        notifyDataSetChanged();
    }
}
