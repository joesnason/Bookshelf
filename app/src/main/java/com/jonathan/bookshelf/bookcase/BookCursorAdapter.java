package com.jonathan.bookshelf.bookcase;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jonathan.bookshelf.R;
import com.jonathan.bookshelf.databases.BookDAO;

/**
 * Created by Jonathan on 2017/3/3.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.search_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView title  = (TextView) view.findViewById(R.id.item_title);
        TextView publish = (TextView) view.findViewById(R.id.item_publish);

        title.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookDAO.FIELD_NAME)));
        publish.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookDAO.FIELD_PUBLISH)));
    }
}
