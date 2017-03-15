package com.jonathan.bookshelf.bookcase;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.CursorSwipeAdapter;
import com.jonathan.bookshelf.R;
import com.jonathan.bookshelf.databases.BookDAO;

/**
 * Created by Jonathan on 2017/3/3.
 */

public class BookCursorAdapter extends CursorSwipeAdapter {

    private final static String TAG = "BookCursorAdapter";
    private View.OnTouchListener mTouchListener;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void closeAllItems() {

    }

    private static class ViewHolder {
        SwipeLayout swipeLayout;
        TextView name;
        TextView publish;
        ImageView cover;
    }

    public BookCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        //mTouchListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item,parent,false);


        ViewHolder viewholder = new ViewHolder();
        viewholder.swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
        viewholder.name = (TextView) view.findViewById(R.id.item_title);
        viewholder.publish = (TextView) view.findViewById(R.id.item_publish);
        viewholder.cover = (ImageView) view.findViewById(R.id.item_cover);
        viewholder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.d(TAG,"onOpen");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
        view.setTag(viewholder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewholder = (ViewHolder) view.getTag();
        viewholder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookDAO.FIELD_NAME)));
        viewholder.publish.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookDAO.FIELD_PUBLISH)));

        String CoverPath = cursor.getString(cursor.getColumnIndexOrThrow(BookDAO.FIELD_COVERLINK));
        viewholder.cover.setImageBitmap(getCoverBmp(CoverPath));
    }

    private Bitmap getCoverBmp(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}
