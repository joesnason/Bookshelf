package com.jonathan.bookshelf.bookcase;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.jonathan.bookshelf.R;
import com.jonathan.bookshelf.databases.BookDAO;

public class SearchActivity extends AppCompatActivity {

    private static String TAG = "SearchActivity";
    private Context mContext;
    private ListView mListView;

    private BookDAO mBookDAO;
    private Cursor mAllBooks;

    private BookCursorAdapter mBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;

        mListView = (ListView) findViewById(R.id.resultlist);

        mBookDAO = new BookDAO(getApplicationContext());
        mAllBooks = mBookDAO.getAll();

        mBookAdapter = new BookCursorAdapter(mContext, mAllBooks, false);
        mListView.setAdapter(mBookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.my_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "get string: " + newText);

                Cursor mBooks = mBookDAO.queryAll(newText);
                mBookAdapter.changeCursor(mBooks);
                return false;
            }
        });

        searchView.setIconifiedByDefault(true);
        return true;
    }
}