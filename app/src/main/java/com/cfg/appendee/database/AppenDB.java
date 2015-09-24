package com.cfg.appendee.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by davem on 24/09/2015.
 */
public class AppenDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="AppenDB.db";

    public AppenDB(Context context){
        super(context, DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(DatabaseContract.SQL_INITIALISE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContract.CREATE_EVENT);
        onCreate(db);
    }
}
