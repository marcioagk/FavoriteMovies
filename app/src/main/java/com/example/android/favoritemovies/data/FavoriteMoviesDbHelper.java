package com.example.android.favoritemovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lsitec26.kanashiro on 20/03/18.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritemovies.db";

    private static final int DATABASE_VERSION = 2;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
                FavoriteMoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
