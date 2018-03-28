package com.example.android.favoritemovies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.favoritemovies.objects.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lsitec26.kanashiro on 15/03/18.
 */

public class MovieReviewJsonUtils {

    private static final String TAG = "MovieReviewJsonUtils";

    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";
    private static final String RESULTS = "results";

    public static Review[] getReviewsFromJson(Context context, String reviewJsonStr) throws JSONException {

        Review[] movieReviews = null;

        JSONObject reviewsJson = new JSONObject(reviewJsonStr);

        JSONArray results = reviewsJson.getJSONArray(RESULTS);

        movieReviews = new Review[results.length()];

        for (int i = 0; i < results.length(); i++) {
            Review movieReview = new Review();

            /* Get the JSON object representing the review */
            JSONObject reviewInfo = results.getJSONObject(i);

            movieReview.setAuthor(reviewInfo.getString(REVIEW_AUTHOR));
            movieReview.setContent(reviewInfo.getString(REVIEW_CONTENT));

            movieReviews[i] = movieReview;
        }

        return movieReviews;
    }
}
