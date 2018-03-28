package com.example.android.favoritemovies.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.android.favoritemovies.R;

/**
 * Created by lsitec26.kanashiro on 15/03/18.
 */

public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

    public TextView mReviewAuthorTextView;
    public TextView mReviewContentTextView;

    public MovieReviewViewHolder(View itemView) {
        super(itemView);
        mReviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
        mReviewContentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
    }

}
