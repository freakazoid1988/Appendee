package com.cfg.appendee.database;

import android.provider.BaseColumns;

/**
 * Created by davem on 24/09/2015.
 */
public final class DatabaseContract implements BaseColumns {

    public static final int DATABASE_VERSION = 1;
    public static final String _ID1 = BaseColumns._ID;
    public static final String NAME = "Name";
    public static final String PLACE = "Place";
    public static final String DATE = "Date";
    public static final String EVENTS = "EVENTS";
    public static final String _ID2="_ID";
    public static final String IN = "In";
    public static final String OUT = "Out";
    public static final String SQL_INITIALISE = "CREATE TABLE " + EVENTS + " (" + _ID1 + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Name TEXT NOT NULL, Place TEXT, Date INTEGER)";
    public static final String FETCH_EVENTS = "SELECT * FROM " + EVENTS;

    public DatabaseContract() {
    }

    public static String createEvent(long insert){
        return "CREATE TABLE "+"\""+Long.toString(insert)+"\""+" ("+_ID2+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Numero INTEGER, Ingresso INTEGER, Uscita INTEGER)";
    }

}
