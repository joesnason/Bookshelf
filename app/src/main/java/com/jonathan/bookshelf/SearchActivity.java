package com.jonathan.bookshelf;

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
import android.widget.SimpleCursorAdapter;

import com.jonathan.bookshelf.databases.BookDAO;

public class SearchActivity extends AppCompatActivity {

    private static String TAG = "SearchActivity";
    private Context mContext;
    private ListView mListView;

    private BookDAO mBookDAO;
    private Cursor mAllBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;

        mListView = (ListView) findViewById(R.id.resultlist);

        mBookDAO = new BookDAO(getApplicationContext());
        mAllBooks = mBookDAO.getAll();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,mAllBooks,
                new String[] {BookDAO.FIELD_NAME, BookDAO.FIELD_AUTHOR },
                new int[] {android.R.id.text1, android.R.id.text2});

        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem menuSearchItem  = menu.findItem(R.id.my_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView  = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"get string: " + newText);
                mAllBooks = mBookDAO.queryAll(newText);

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(mContext,android.R.layout.simple_list_item_2,mAllBooks,
                        new String[] {BookDAO.FIELD_NAME, BookDAO.FIELD_AUTHOR },
                        new int[] {android.R.id.text1, android.R.id.text2});

                mListView.setAdapter(adapter);

                return false;
            }
        });

        searchView.setIconifiedByDefault(true);

        return true;
    }
}
