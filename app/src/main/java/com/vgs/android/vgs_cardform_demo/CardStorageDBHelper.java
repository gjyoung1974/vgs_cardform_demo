package com.vgs.android.vgs_cardform_demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//A class to manage our local SQLite Database
public class CardStorageDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + "cards_table" + " (" +
                    "cards_table_ID" + " INTEGER PRIMARY KEY," +
                    "cards_table_COLUMN_NAME_CARDIDENTIFIER" + " TEXT," +
                    "cards_table_COLUMN_NAME_CARDTYPE" + " TEXT," +
                    "cards_table_COLUMN_NAME_CCN" + " TEXT," +
                    "cards_table_COLUMN_NAME_CVV" + " TEXT," +
                    "cards_table_COLUMN_NAME_MONTH" + " TEXT," +
                    "cards_table_COLUMN_NAME_YEAR" + " TEXT," +
                    "cards_table_COLUMN_POST_CODE" + " TEXT," +
                    "cards_table_COLUMN_COUNTRYCOE" + " TEXT," +
                    "cards_table_COLUMN_NAME_MOBILE" + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + "cards_table";


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "vgs_cardform_demo.db";

    public CardStorageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}