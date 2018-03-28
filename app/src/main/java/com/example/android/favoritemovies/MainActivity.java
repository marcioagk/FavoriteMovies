package com.example.android.favoritemovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.favoritemovies.adapters.MovieAdapter;
import com.example.android.favoritemovies.data.FavoriteMoviesContract;
import com.example.android.favoritemovies.objects.Movie;
import com.example.android.favoritemovies.utilities.MovieJsonUtils;
import com.example.android.favoritemovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Movie[]> {

    private static final String TAG = "MainActivity";

    private static final String MOVIES_FROM_URL_EXTRA = "movies_from_url";

    private static final int MOVIES_FROM_URL_LOADER = 11;
    private static final int MOVIES_FROM_DB_LOADER = 12;

    private static final String SHARED_PREFERENCES = "shared_preferences";
    private static final String VIEW_PREFERENCE = "view_preference";
    private static final String MOST_POPULAR_PREFERENCE = "most_popular";
    private static final String TOP_RATED_PREFERENCE = "top_rated";
    private static final String MY_FAVORITE_PREFERENCE = "my_favorite";

    private static final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String TOP_RATED_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageTextView;
    private MovieAdapter mMovieAdapter;

    // TODO 1: Please, insert your API Key at the string constant below
    public static final String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager layout = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layout);

        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        String viewOption = getViewSharedPreferences();
        if (viewOption.equals(MOST_POPULAR_PREFERENCE)) {
            setTitle(getString(R.string.popular_movies));
            loadFamousMovies(POPULAR_MOVIES_BASE_URL);
        } else if (viewOption.equals(TOP_RATED_PREFERENCE)) {
            setTitle(getString(R.string.top_rated_movies));
            loadFamousMovies(TOP_RATED_MOVIES_BASE_URL);
        } else if (viewOption.equals(MY_FAVORITE_PREFERENCE)) {
            setTitle(getString(R.string.my_favorite_movies));
            loadMyFavoriteMovies();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String viewOption = getViewSharedPreferences();
        if (viewOption.equals(MY_FAVORITE_PREFERENCE)) {
            setTitle(getString(R.string.my_favorite_movies));
            loadMyFavoriteMovies();
        }
    }

    private void loadFamousMovies(String baseUrl) {
        showMoviesPainel();

        String popularMovies = baseUrl + API_KEY;

        Bundle bundle = new Bundle();
        bundle.putString(MOVIES_FROM_URL_EXTRA, popularMovies);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie[]> movieLoader = loaderManager.getLoader(MOVIES_FROM_URL_LOADER);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIES_FROM_URL_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_FROM_URL_LOADER, bundle, this);
        }
    }

    private void loadMyFavoriteMovies() {
        showMoviesPainel();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Movie[]> movieLoader = loaderManager.getLoader(MOVIES_FROM_DB_LOADER);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIES_FROM_DB_LOADER, null, this);
        } else {
            loaderManager.restartLoader(MOVIES_FROM_DB_LOADER, null, this);
        }
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showMoviesPainel() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = MainActivity.this;
        Class destinationActivity = MovieDetailsActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    /* LOADER */
    @Override
    public Loader<Movie[]> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Movie[]>(this) {

            Movie[] mMovieList = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mMovieList != null) {
                    deliverResult(mMovieList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {

                switch (id) {
                    case MOVIES_FROM_URL_LOADER:
                        String famousMoviesBaseUrl = args.getString(MOVIES_FROM_URL_EXTRA);

                        if (famousMoviesBaseUrl == null || TextUtils.isEmpty(famousMoviesBaseUrl)) {
                            return null;
                        }

                        URL famousMoviesUrl = NetworkUtils.buildUrl(famousMoviesBaseUrl);
                        try {
                            String moviesResponseJson = NetworkUtils.getResponseFromHttpUrl(famousMoviesUrl);
                            mMovieList = MovieJsonUtils.getMoviesFromJson(MainActivity.this, moviesResponseJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        break;
                    case MOVIES_FROM_DB_LOADER:
                        Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                        if (cursor.moveToFirst()) {
                            mMovieList = new Movie[cursor.getCount()];
                            for (int i = 0; i < cursor.getCount(); i++) {
                                if (cursor.moveToPosition(i)) {
                                    Movie movie = new Movie();
                                    movie.setMovieId(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)));
                                    movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE)));
                                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH)));
                                    movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_OVERVIEW)));
                                    movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE)));
                                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE)));
                                    mMovieList[i] = movie;
                                }
                            }
                            cursor.close();
                        } else {
                            return null;
                        }
                        break;
                    default:
                        return null;
                }
                return mMovieList;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        if (data != null) {
            mMovieAdapter.setMoviesData(data);
        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    /* OPTIONS MENU */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular_movies) {
            setTitle(getString(R.string.popular_movies));
            loadFamousMovies(POPULAR_MOVIES_BASE_URL);
            setViewSharedPreferences(MOST_POPULAR_PREFERENCE);
            return true;
        }

        if (id == R.id.action_top_rated_movies) {
            setTitle(getString(R.string.top_rated_movies));
            loadFamousMovies(TOP_RATED_MOVIES_BASE_URL);
            setViewSharedPreferences(TOP_RATED_PREFERENCE);
            return true;
        }

        if (id == R.id.action_my_favorite_movies) {
            setTitle(getString(R.string.my_favorite_movies));
            loadMyFavoriteMovies();
            setViewSharedPreferences(MY_FAVORITE_PREFERENCE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* SHARED PREFERENCES */
    private String getViewSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String viewMode = sharedPreferences.getString(VIEW_PREFERENCE, MOST_POPULAR_PREFERENCE);
        return viewMode;
    }

    private void setViewSharedPreferences(String preference) {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(VIEW_PREFERENCE, preference);
        editor.apply();
    }
}
