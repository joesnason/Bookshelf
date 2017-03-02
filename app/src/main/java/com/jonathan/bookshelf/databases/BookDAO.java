package com.jonathan.bookshelf.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jonathan on 2017/2/17.
 */

public class BookDAO {

    public static final String TABLE_NAME = "Books";

    //  table field
    public static final String KEY_ID = "_id";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_ISBN = "ISBN";
    public static final String FIELD_AUTHOR = "Author";
    public static final String FIELD_PUBLISH = "Publish";
    public static final String FIELD_COVERLINK = "CoverLink";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_NAME + " TEXT NOT NULL, " +
                    FIELD_ISBN + " TEXT NOT NULL, " +
                    FIELD_AUTHOR + " TEXT, " +
                    FIELD_PUBLISH + " TEXT, " +
                    FIELD_COVERLINK + " TEXT)";

    private SQLiteDatabase db;

    public BookDAO (Context context){
        db = BookDBHelper.getDatabase(context);
    }

    public void close(){
        db.close();
    }

    public Book insert(Book book){

        ContentValues record = new ContentValues();
        record.put(FIELD_NAME, book.getName());
        record.put(FIELD_ISBN, book.getISBN());
        record.put(FIELD_AUTHOR, book.getAuthor());
        record.put(FIELD_PUBLISH, book.getPublish());
        record.put(FIELD_COVERLINK, book.getCoverLink());

        long id = db.insert(TABLE_NAME, null, record);
        book.setID(id);

        return book;

    }

    public boolean update(Book book){

        ContentValues record = new ContentValues();
        record.put(FIELD_NAME, book.getName());
        record.put(FIELD_ISBN, book.getISBN());
        record.put(FIELD_AUTHOR, book.getAuthor());
        record.put(FIELD_PUBLISH, book.getPublish());
        record.put(FIELD_COVERLINK, book.getCoverLink());

        String where = KEY_ID + "=" + book.getID();

        return db.update(TABLE_NAME, record, where, null) > 0;

    }

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;

        return db.delete(TABLE_NAME,where,null) > 0;

    }

    public Book queryISBN(String ISBN){

        Book book = null;

        String where = FIELD_ISBN + "=" + ISBN;
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);


        if(result.moveToFirst()){
            book = getRecord(result);
        }

        result.close();

        return book;

    }

    public Cursor queryAll(String keyword){

        if(keyword.length() !=0){
            keyword = "%" + keyword + "%";
        }

        String SQL = "SELECT * FROM " + TABLE_NAME + " WHERE " + FIELD_ISBN + " like ?";

        Cursor result = db.rawQuery(SQL,new String[] {keyword});

        return result;

    }

    public Cursor getAll(){
        Cursor result = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        return result;
    }


    private Book getRecord(Cursor cursor){
        Book book = new Book();

        book.setID(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        book.setName(cursor.getString(cursor.getColumnIndex(FIELD_NAME)));
        book.setISBN(cursor.getString(cursor.getColumnIndex(FIELD_ISBN)));
        book.setAuthor(cursor.getString(cursor.getColumnIndex(FIELD_AUTHOR)));
        book.setPublish(cursor.getString(cursor.getColumnIndex(FIELD_PUBLISH)));
        book.setCoverLink(cursor.getString(cursor.getColumnIndex(FIELD_COVERLINK)));


        return book;
    }

}
