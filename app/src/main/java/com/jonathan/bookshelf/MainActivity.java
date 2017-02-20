package com.jonathan.bookshelf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jonathan.bookshelf.databases.Book;
import com.jonathan.bookshelf.databases.BookDAO;
import com.jonathan.bookshelf.parser.BooksParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static private String TAG = "Bookshelf";
    static private int  REQUEST_CAMERA = 1;
    static private int  REQUEST_WRITE_STORAGE = 2;

    private final int UPDATE_BOOK_NAME = 1;
    private final int UPDATE_BOOK_AUTHOR = 2;
    private final int UPDATE_BOOK_PUBLISH = 3;
    private final int UPDATE_BOOK_COVER = 4;

    private Activity mMainActivity;
    private Button mScan_btn;
    private Button mSave_btn;
    private TextView mBookID;
    private TextView mBookName;
    private TextView mBookAuthor;
    private TextView mBookPublish;
    private TextView mNotice;
    private ImageView mBookCover;

    private BookDAO bookDAO;

    private UIHandler mUIHandler;
    private Thread parserThread;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mMainActivity = this;

        init_view();

        mUIHandler = new UIHandler();

        bookDAO = new BookDAO(getApplicationContext());

        mScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(mMainActivity);
                scanIntegrator.initiateScan();
            }
        });


        mSave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book(mBookID.getText().toString());
                book.setName(mBookName.getText().toString());
                book.setAuthor(mBookAuthor.getText().toString());
                book.setPublish(mBookPublish.getText().toString());

                Bitmap CoverBmp = ((BitmapDrawable)mBookCover.getDrawable()).getBitmap();
                if(SaveBmp(CoverBmp,book.getISBN())) {
                    book.setCoverLink(Environment.DIRECTORY_PICTURES + "/Boookshelf/" + book.getISBN() + ".png");
                    Log.d(TAG, "Save cover file finish");
                }

            }
        });


        // Check if the Camera permission is already available.
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // already available
            mScan_btn.setEnabled(true);
        } else {
            mScan_btn.setEnabled(false);
            // asking whether to allow permission...
            requestCameraPermission();
        }

        if(ContextCompat.checkSelfPermission(mMainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestWriteStoragePermission();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult!=null){
            String scanContent = scanningResult.getContents();
            mBookID.setText(scanContent);
            parserThread = new Thread(new parseHTMLTask());
            parserThread.start();

            Book book = bookDAO.query(scanContent);
            if (book != null) {
                Log.d(TAG, "I have this book");
                mNotice.setText("I have had this book!");
            } else {
                mNotice.setText("");
            }


        }else{
            Toast.makeText(getApplicationContext(),"nothing",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                mScan_btn.setEnabled(true);
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                mScan_btn.setEnabled(false);
            }
        }
        // other callback
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void init_view(){
        mBookCover = (ImageView) findViewById(R.id.bookcover);
        mScan_btn = (Button)findViewById(R.id.scan);
        mBookID = (TextView) findViewById(R.id.bookid);
        mBookName = (TextView) findViewById(R.id.bookname);
        mBookAuthor = (TextView) findViewById(R.id.author);
        mBookPublish = (TextView) findViewById(R.id.publish);
        mNotice = (TextView) findViewById(R.id.notice);
        mSave_btn = (Button) findViewById(R.id.save);
    }


    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i( TAG, "CAMERA permission has NOT been granted. Requesting permission." ) ;

        if (ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.CAMERA) ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG, "Displaying camera permission rationale to provide additional context.") ;


        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA) ;
        }
    }

    private void requestWriteStoragePermission() {
        Log.i( TAG, "WRITE EXTERNAL STORAGE permission has NOT been granted. Requesting permission." ) ;

        if (ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG, "WRITE EXTERNAL STORAGE permission rationale to provide additional context.") ;


        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE) ;
        }
    }

    private Boolean SaveBmp(Bitmap bmp, String name){
        FileOutputStream out = null;
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/Boookshelf");
        File file = new File(path, name + ".png");
        try {
            // create directory
            path.mkdirs();

            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private class parseHTMLTask implements Runnable {

        @Override
        public void run() {
            try {

                BooksParser parser = new BooksParser(mBookID.getText().toString());

                // book cover first
                Bitmap bmp = parser.getBookCover();
                Message updateCover = mUIHandler.obtainMessage(UPDATE_BOOK_COVER,bmp);
                mUIHandler.sendMessage(updateCover);

                //find book name title
                String name  = parser.getBookName();
                Log.d(TAG,"book name: " + name);
                Message msg = mUIHandler.obtainMessage(UPDATE_BOOK_NAME,name);
                mUIHandler.sendMessage(msg);

                String author = parser.getBookAuthor();
                Log.d(TAG,"book author: " + author);
                Message updateAuthor = mUIHandler.obtainMessage(UPDATE_BOOK_AUTHOR,author);
                mUIHandler.sendMessage(updateAuthor);

                String publish = parser.getBookPublish();
                Log.d(TAG,"book publish: " + publish);
                Message updatePublish = mUIHandler.obtainMessage(UPDATE_BOOK_PUBLISH,publish);
                mUIHandler.sendMessage(updatePublish);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class UIHandler extends Handler {
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_BOOK_NAME:
                    mBookName.setText((String)msg.obj);
                    break;
                case UPDATE_BOOK_AUTHOR:
                    mBookAuthor.setText((String)msg.obj);
                    break;
                case UPDATE_BOOK_PUBLISH:
                    mBookPublish.setText((String)msg.obj);
                    break;
                case UPDATE_BOOK_COVER:
                    mBookCover.setVisibility(View.VISIBLE);
                    mBookCover.setImageBitmap((Bitmap)msg.obj);
                default:
                    break;
            }
        }
    };
}
