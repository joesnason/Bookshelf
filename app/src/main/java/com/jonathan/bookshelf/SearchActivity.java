package com.jonathan.bookshelf;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jonathan.bookshelf.databases.BookDAO;

public class SearchActivity extends AppCompatActivity {

    private ListView mListView;

    private BookDAO mBookDAO;
    private Cursor mAllBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListView = (ListView) findViewById(R.id.resultlist);

        mBookDAO = new BookDAO(getApplicationContext());
        mAllBooks = mBookDAO.getAll();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,mAllBooks,
                new String[] {BookDAO.FIELD_NAME, BookDAO.FIELD_AUTHOR },
                new int[] {android.R.id.text1, android.R.id.text2});

        mListView.setAdapter(adapter);

    }
}
