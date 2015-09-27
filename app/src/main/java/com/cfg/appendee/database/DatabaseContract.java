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
    public static final String INGRESSO = "Ingresso";
    public static final String USCITA = "Uscita";
    public static final String NUMBER = "Number";
    public static final String SQL_INITIALISE = "CREATE TABLE " + EVENTS + " (" + _ID1 + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + NAME + " TEXT NOT NULL, " + PLACE + " TEXT, " + DATE + " INTEGER)";
    public static final String FETCH_EVENTS = "SELECT * FROM " + EVENTS;

    public DatabaseContract() {
    }

    public static String createEvent(long insert){
        return "CREATE TABLE " + "\"" + Long.toString(insert) + "\"" + " (" + _ID2 + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + NUMBER + " INTEGER, " + INGRESSO + " INTEGER, " + USCITA + " INTEGER)";
    }

    /**
     * @param tablename
     * @return A query to retrieve the participants from a given event
     */
    public static String fetchParticipants(String tablename) {
        return "SELECT * FROM " + tablename;
    }

}
