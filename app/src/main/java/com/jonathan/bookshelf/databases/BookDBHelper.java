package com.jonathan.bookshelf.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathan on 2017/2/17.
 */

public class BookDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mybooks.db";
    private static final int VERSION = 1;

    private static SQLiteDatabase database;

    public BookDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context){

        if(database == null || !database.isOpen()){
            database = new BookDBHelper(context,DATABASE_NAME,null,VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(BookDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + BookDAO.TABLE_NAME);
        onCreate(db);
    }
}
