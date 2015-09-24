package com.cfg.appendee.database;

/**
 * Created by davem on 24/09/2015.
 */
public final class DatabaseContract {

    public DatabaseContract(){}

    public static final int DATABASE_VERSION = 1;
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String PLACE = "Place";
    public static final String DATE = "Date";

    public static final String IN = "In";
    public static final String OUT = "Out";

    public static final String SQL_INITIALISE="CREATE TABLE Events (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, Name TEXT NOT NULL, Place TEXT, Date INTEGER)";

    public static final String CREATE_EVENT="CREATE TABLE Event (ID INTEGER UNIQUE, In INTEGER, Out INTEGER)";

}
