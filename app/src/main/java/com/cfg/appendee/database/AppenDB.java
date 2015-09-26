package com.cfg.appendee.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by davem on 24/09/2015.
 */
public class AppenDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="AppenDB.db";
    private static String DB_PATH ="";
    private final Context mContext;
    private SQLiteDatabase mDataBase;

    public AppenDB(Context context){
        super(context, DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.mContext = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(DatabaseContract.SQL_INITIALISE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.EVENTS);
        onCreate(db);
    }

    public void createDataBase() throws IOException {
        // Bisogna controllare che il database non esista già prima di creare il file di db altrimenti viene azzerato ogni volta
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            //crei un file che dovrai usare come output del database (o lo carichi dalla cartella Assets se vuoi partire da un database precompilato incluso nell'APK)
            //il file lo puoi creare con i classici metodi di I/O di java es
            File dbFile = new File(DB_PATH + DATABASE_NAME);
            //e ovviamente puoi eliminarli facendo una flush sempre con i classici metodi di I/O di Java
        }
    }

    // Controlla che il database esista
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }
}
