package com.example.android.favoritemovies.utilities;

import android.content.Context;

import com.example.android.favoritemovies.objects.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lsitec26.kanashiro on 20/11/17.
 */

public class MovieJsonUtils {

    private static final String TAG = "MovieJsonUtils";

    private static final String MOVIE_ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String RESULTS = "results";

    public static Movie[] getMoviesFromJson(Context context, String moviesJsonStr) throws JSONException {

        Movie[] movieList = null;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        JSONArray results = moviesJson.getJSONArray(RESULTS);

        movieList = new Movie[results.length()];

        for (int i = 0; i < results.length(); i++) {

            Movie movie = new Movie();

            /* Get the JSON object representing the movie */
            JSONObject movieInfo = results.getJSONObject(i);

            movie.setMovieId(movieInfo.getString(MOVIE_ID));
            movie.setOriginalTitle(movieInfo.getString(ORIGINAL_TITLE));
            movie.setPosterPath(movieInfo.getString(POSTER_PATH));
            movie.setOverview(movieInfo.getString(OVERVIEW));
            movie.setVoteAverage(movieInfo.getString(VOTE_AVERAGE));
            movie.setReleaseDate(movieInfo.getString(RELEASE_DATE));

            movieList[i] = movie;

        }

        return movieList;
    }

}
