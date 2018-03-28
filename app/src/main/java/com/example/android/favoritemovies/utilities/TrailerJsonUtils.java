package com.example.android.favoritemovies.utilities;

import android.content.Context;

import com.example.android.favoritemovies.objects.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lsitec26.kanashiro on 16/03/18.
 */

public class TrailerJsonUtils {

    private static final String TAG = "TrailerJsonUtils";

    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_SITE = "site";
    private static final String TRAILER_TYPE = "type";
    private static final String RESULTS = "results";

    public static Trailer[] getTrailerFromJson(Context context, String trailerJsonStr) throws JSONException {

        Trailer[] trailers = null;

        JSONObject trailersJson = new JSONObject(trailerJsonStr);

        JSONArray results = trailersJson.getJSONArray(RESULTS);

        trailers = new Trailer[results.length()];

        for (int i = 0 ; i < results.length(); i++) {
            Trailer trailer = new Trailer();
            JSONObject trailerInfo = results.getJSONObject(i);

            trailer.setKey(trailerInfo.getString(TRAILER_KEY));
            trailer.setName(trailerInfo.getString(TRAILER_NAME));
            trailer.setSite(trailerInfo.getString(TRAILER_SITE));
            trailer.setType(trailerInfo.getString(TRAILER_TYPE));

            trailers[i] = trailer;
        }

        return trailers;
    }

}
