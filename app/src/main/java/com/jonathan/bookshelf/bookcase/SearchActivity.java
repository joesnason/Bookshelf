package com.jonathan.bookshelf.bookcase;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.jonathan.bookshelf.R;
import com.jonathan.bookshelf.databases.BookDAO;

import org.jsoup.select.Evaluator;

public class SearchActivity extends AppCompatActivity {

    private static String TAG = "SearchActivity";
    private Context mContext;
    private ListView mListView;

    private BookDAO mBookDAO;
    private Cursor mAllBooks;

    private BookCursorAdapter mBookAdapter;

    // for touch event
    private Boolean isItemPress = false;
    private Boolean isSwiping = false;
    private float mDownX = 0;
    private int mSwipeSlop = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;

        mListView = (ListView) findViewById(R.id.resultlist);

        mBookDAO = new BookDAO(getApplicationContext());
        mAllBooks = mBookDAO.getAll();

        mBookAdapter = new BookCursorAdapter(mContext, mAllBooks, false);
        mBookAdapter.setMode(Attributes.Mode.Single);

        mListView.setAdapter(mBookAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout)(mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });
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

//    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            if (mSwipeSlop < 0) {
//                mSwipeSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
//                Log.d(TAG, "SwipeSlop: " + mSwipeSlop);
//            }
//
//            int action = MotionEventCompat.getActionMasked(event);
//            switch (action){
//                case MotionEvent.ACTION_DOWN:
//                    Log.d(TAG,"Action down");
//                    if(isItemPress){
//                        // skip multi-touch event
//                        return false;
//                    }
//
//                    isItemPress = true;
//                    mDownX = event.getX();
//                    break;
//
//                case MotionEvent.ACTION_UP:
//                    Log.d(TAG,"action up");
//                    isItemPress = false;
//                    isSwiping = false;
//                    break;
//
//                case MotionEvent.ACTION_CANCEL:
//                    v.setAlpha(1);
//                    v.setTranslationX(0);
//                    isItemPress = false;
//                    isSwiping = false;
//                    break;
//
//                case MotionEvent.ACTION_MOVE:
//                    Log.d(TAG,"Action Move");
//
//                    float x = event.getX() + v.getTranslationX();
//                    float deltaX = x - mDownX;
//                    float deltaXabs = Math.abs(deltaX);
//                    if(!isSwiping && deltaXabs > mSwipeSlop){
//                        isSwiping = true;
//                        mListView.requestDisallowInterceptTouchEvent(true);
//                    }
//                    Log.d(TAG,"Action Move deltaX: " + deltaX);
//                    if(isSwiping && deltaXabs < (v.getWidth() / 3)){
//                        v.setTranslationX(deltaX);
//                        v.setAlpha(1 - deltaXabs / v.getWidth());
//                    }
//                    break;
//                default:
//                    return true;
//            }
//            return true;
//        }
//    };
}
