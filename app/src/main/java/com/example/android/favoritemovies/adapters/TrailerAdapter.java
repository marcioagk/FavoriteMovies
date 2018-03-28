package com.example.android.favoritemovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.favoritemovies.R;
import com.example.android.favoritemovies.holders.TrailerViewHolder;
import com.example.android.favoritemovies.objects.Trailer;

/**
 * Created by lsitec26.kanashiro on 19/03/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TrailerAdapter";

    private Trailer[] mTrailers;
    private Context context;

    private final TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        TrailerViewHolder holder = new TrailerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TrailerViewHolder holder = (TrailerViewHolder) viewHolder;
        int trailerNumber = position + 1;
        holder.mTrailerLabelTextView.setText(context.getString(R.string.trailer) + " " + trailerNumber + " (" + mTrailers[position].getName() + ")");
        holder.bind(mTrailers[position], mClickHandler);
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.length;
    }

    public void setTrailers(Trailer[] trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }
}
