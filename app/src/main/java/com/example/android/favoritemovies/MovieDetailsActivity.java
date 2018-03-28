package com.example.android.favoritemovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.favoritemovies.adapters.MovieReviewAdapter;
import com.example.android.favoritemovies.adapters.TrailerAdapter;
import com.example.android.favoritemovies.data.FavoriteMoviesContract;
import com.example.android.favoritemovies.objects.Movie;
import com.example.android.favoritemovies.objects.Review;
import com.example.android.favoritemovies.objects.Trailer;
import com.example.android.favoritemovies.utilities.MovieReviewJsonUtils;
import com.example.android.favoritemovies.utilities.NetworkUtils;
import com.example.android.favoritemovies.utilities.TrailerJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MovieDetailsActivity";

    /* A constant to save and restore the URL that is being displayed */
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String MORE_DETAILS_URL_EXTRA = "more_details_extra";

    private static final int MOVIE_REVIEW_LOADER = 21;
    private static final int MOVIE_TRAILER_LOADER = 22;

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String MOVIE_TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/%s/videos?api_key=%s";
    private static final String MOVIE_REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/%s/reviews?api_key=%s";

    // TODO 1: Please, insert your API Key at the string constant below
//    private static final String API_KEY = "8f360636039d133228cd094cec65951e";

    private NestedScrollView mMovieDetailsScreenScrollView;
    private TextView mOriginalTitleHeaderTextView;
    private ImageView mPosterImageView;
    private TextView mVoteAverageTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;
    private ImageButton mFavoriteMovieImageButton;

    //MOVIE REVIEW
    private RecyclerView mReviewRecyclerView;
    private ImageButton mReviewIconImageButton;
    private MovieReviewAdapter mReviewAdapter;

    //TRAILERS
    private ImageButton mTrailerIconImageButton;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private Movie mMovieItem;
    private String mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mMovieDetailsScreenScrollView = (NestedScrollView) findViewById(R.id.sv_movie_details_screen);
        mOriginalTitleHeaderTextView = (TextView) findViewById(R.id.tv_original_title_header);
        mPosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_movie_vote_average);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        mOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mFavoriteMovieImageButton = (ImageButton) findViewById(R.id.ib_favorite_movie);

        //MOVIE REVIEW
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_review);
        mReviewIconImageButton = (ImageButton) findViewById(R.id.ib_review_icon);

        mReviewAdapter = new MovieReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TRAILERS
        mTrailerIconImageButton = (ImageButton) findViewById(R.id.ib_trailer_icon);
        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_trailer);

        mTrailerAdapter = new TrailerAdapter(this, new TrailerAdapter.TrailerAdapterOnClickHandler() {
            @Override
            public void onClick(Trailer trailer) {
                playVideo(trailer);
            }
        });
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMovieItem = new Movie();
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_MOVIE)) {
                mMovieItem = (Movie) intentThatStartedThisActivity.getParcelableExtra(EXTRA_MOVIE);
                mMovieId = mMovieItem.getMovieId();
                mOriginalTitleHeaderTextView.setText(mMovieItem.getOriginalTitle());
                mVoteAverageTextView.setText(mMovieItem.getVoteAverage());
                mReleaseDateTextView.setText(mMovieItem.getReleaseDate());
                mOverviewTextView.setText(mMovieItem.getOverview());
                String movieURL = MOVIE_POSTER_BASE_URL + mMovieItem.getPosterPath();
                Picasso.with(this).load(movieURL).into(mPosterImageView);
            }
        }

        isFavoriteMovie();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("TRAILERS_VISIBLE", mTrailerRecyclerView.getVisibility() == View.VISIBLE);
        outState.putBoolean("REVIEWS_VISIBLE", mReviewRecyclerView.getVisibility() == View.VISIBLE);
        outState.putIntArray("SCREEN_POSITION", new int[]{ mMovieDetailsScreenScrollView.getScrollX(), mMovieDetailsScreenScrollView.getScrollY() });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            boolean isTrailersOpen = savedInstanceState.getBoolean("TRAILERS_VISIBLE");
            boolean isReviewsOpen = savedInstanceState.getBoolean("REVIEWS_VISIBLE");
            final int[] position = savedInstanceState.getIntArray("SCREEN_POSITION");

            if (isTrailersOpen) {
                showTrailerList(mTrailerRecyclerView);
            }

            if (isReviewsOpen) {
                showMovieReviewList(mReviewRecyclerView);
            }

            mMovieDetailsScreenScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mMovieDetailsScreenScrollView.scrollTo(position[0], position[1]);
                }
            });
        }
    }

    private void playVideo(Trailer trailer) {
        Log.d(TAG, "playVideo: " + trailer.getName());
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    /*
    ##################
    #### on Click ####
    ##################
     */
    public void markAsFavorite(View view) {
        ImageButton favoriteImageButton = (ImageButton) view;
        long tag = (long) favoriteImageButton.getTag();
        if (tag <= 0){
            favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_on);
            favoriteImageButton.setTag(addToFavorites(mMovieItem));
        } else {
            favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_off);
            if (removeFromFavorites(tag)) {
                favoriteImageButton.setTag(0L);
            }
        }
    }

    public void showTrailerList(View view) {
        if (mTrailerRecyclerView.getVisibility() == View.INVISIBLE || mTrailerRecyclerView.getVisibility() == View.GONE) {
            String trailerBaseUrl;
            trailerBaseUrl = String.format(MOVIE_TRAILER_BASE_URL, mMovieId, MainActivity.API_KEY);
            URL trailerUrl = NetworkUtils.buildUrl(trailerBaseUrl);

            Bundle trailerBundle = new Bundle();
            trailerBundle.putString(MORE_DETAILS_URL_EXTRA, trailerUrl.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> trailerLoader = loaderManager.getLoader(MOVIE_TRAILER_LOADER);

            if (trailerLoader == null) {
                loaderManager.initLoader(MOVIE_TRAILER_LOADER, trailerBundle, MovieDetailsActivity.this);
            } else {
                loaderManager.restartLoader(MOVIE_TRAILER_LOADER, trailerBundle, MovieDetailsActivity.this);
            }

            mTrailerRecyclerView.setVisibility(View.VISIBLE);
            mTrailerIconImageButton.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            mTrailerRecyclerView.setVisibility(View.GONE);
            mTrailerIconImageButton.setImageResource(android.R.drawable.arrow_down_float);
        }
    }

    public void showMovieReviewList(View view) {
        if (mReviewRecyclerView.getVisibility() == View.INVISIBLE || mReviewRecyclerView.getVisibility() == View.GONE) {
            String movieReviewBaseUrl;
            movieReviewBaseUrl = String.format(MOVIE_REVIEW_BASE_URL, mMovieId, MainActivity.API_KEY);
            URL movieReviewUrl = NetworkUtils.buildUrl(movieReviewBaseUrl);

            Bundle movieReviewBundle = new Bundle();
            movieReviewBundle.putString(MORE_DETAILS_URL_EXTRA, movieReviewUrl.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> movieReviewLoader = loaderManager.getLoader(MOVIE_REVIEW_LOADER);

            if (movieReviewLoader == null) {
                loaderManager.initLoader(MOVIE_REVIEW_LOADER, movieReviewBundle, MovieDetailsActivity.this);
            } else {
                loaderManager.restartLoader(MOVIE_REVIEW_LOADER, movieReviewBundle, MovieDetailsActivity.this);
            }

            mReviewRecyclerView.setVisibility(View.VISIBLE);
            mReviewIconImageButton.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            mReviewRecyclerView.setVisibility(View.GONE);
            mReviewIconImageButton.setImageResource(android.R.drawable.arrow_down_float);
        }
    }

    /*
    ##################
    #### DATABASE ####
    ##################
     */
    private void isFavoriteMovie () {
        Cursor c = getContentResolver().query(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                null,
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID  + " = ?",
                new String[] {mMovieItem.getMovieId()},
                null);

        if (c.moveToFirst()) {
            mFavoriteMovieImageButton.setImageResource(android.R.drawable.btn_star_big_on);
            mFavoriteMovieImageButton.setTag(c.getLong(c.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry._ID)));
        } else {
            mFavoriteMovieImageButton.setImageResource(android.R.drawable.btn_star_big_off);
            mFavoriteMovieImageButton.setTag(0L);
        }
        c.close();
    }

    private long addToFavorites(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

        Uri uri = getContentResolver().insert(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            return Long.valueOf(uri.getLastPathSegment());
        } else {
            return 0L;
        }
    }

    private boolean removeFromFavorites(long id) {
        String stringId = Long.toString(id);
        Uri uri = FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        int itensDeleted = getContentResolver().delete(uri, null, null);

        if (itensDeleted > 0)
            return true;
        else
            return false;

    }

    /*
    ##################
    ##### LOADER #####
    ##################
     */
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mMovieReviewJson;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading");

                if (args == null) {
                    return;
                }

                if (mMovieReviewJson != null) {
                    deliverResult(mMovieReviewJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                Log.d(TAG, "loadInBackground");
                String movieReviewUrlString = args.getString(MORE_DETAILS_URL_EXTRA);
                if (movieReviewUrlString == null || TextUtils.isEmpty(movieReviewUrlString)) {
                    return null;
                }

                try {
                    URL movieReviewUrl = new URL(movieReviewUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(movieReviewUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                Log.d(TAG, "deliverResult");
                mMovieReviewJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "onLoadFinished");
        if (data != null && !data.equals("")) {
            switch (loader.getId()) {
                case MOVIE_REVIEW_LOADER:
                    Log.d(TAG, "onLoadFinished - MOVIE_REVIEW_LOADER");
                    Review[] reviews = null;
                    try {
                        reviews = MovieReviewJsonUtils.getReviewsFromJson(MovieDetailsActivity.this, data);
                        mReviewAdapter.setReviews(reviews);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MOVIE_TRAILER_LOADER:
                    Log.d(TAG, "onLoadFinished - MOVIE_TRAILER_LOADER");
                    Trailer[] trailers = null;

                    try {
                        trailers = TrailerJsonUtils.getTrailerFromJson(MovieDetailsActivity.this, data);
                        mTrailerAdapter.setTrailers(trailers);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
