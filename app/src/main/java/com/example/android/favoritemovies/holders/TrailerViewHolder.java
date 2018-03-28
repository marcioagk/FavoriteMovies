package com.example.android.favoritemovies.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.favoritemovies.R;
import com.example.android.favoritemovies.adapters.TrailerAdapter;
import com.example.android.favoritemovies.objects.Trailer;

/**
 * Created by lsitec26.kanashiro on 19/03/18.
 */

public class TrailerViewHolder extends RecyclerView.ViewHolder {

    public TextView mTrailerLabelTextView;

    public TrailerViewHolder(View itemView) {
        super(itemView);
        mTrailerLabelTextView = (TextView) itemView.findViewById(R.id.tv_trailer_label);
    }

    public void bind(final Trailer trailer, final TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickHandler.onClick(trailer);
            }
        });
    }
}
