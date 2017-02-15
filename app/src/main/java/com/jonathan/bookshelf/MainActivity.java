package com.jonathan.bookshelf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static private String TAG = "Bookshelf";
    static private int  REQUEST_CAMERA = 1;

    private final int UPDATE_BOOK_NAME = 1;
    private final int UPDATE_BOOK_AUTHOR = 2;
    private final int UPDATE_BOOK_PUBLISH = 3;

    private Activity mMainActivity;
    private Button scan_btn;
    private EditText mBookID;
    private TextView mBookName;
    private TextView mBookAuthor;
    private TextView mBookPublish;

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

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(mMainActivity);
                scanIntegrator.initiateScan();
            }
        });


        // Check if the Camera permission is already available.
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // already available
            scan_btn.setEnabled(true);
        } else {
            scan_btn.setEnabled(false);
            // asking whether to allow permission...
            requestCameraPermission();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult!=null){
            String scanContent=scanningResult.getContents();
            mBookID.setText(scanContent);
            parserThread = new Thread(new parseHTMLTask());
            parserThread.start();
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
                scan_btn.setEnabled(true);
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                scan_btn.setEnabled(false);
            }
        }
        // other callback
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void init_view(){
        scan_btn = (Button)findViewById(R.id.scan);
        mBookID = (EditText) findViewById(R.id.bookid);
        mBookName = (TextView) findViewById(R.id.bookname);
        mBookAuthor = (TextView) findViewById(R.id.author);
        mBookPublish = (TextView) findViewById(R.id.publish);
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

    private class parseHTMLTask implements Runnable {

        @Override
        public void run() {
            try {

                url= new URL(Config.QUERY_URL + mBookID.getText());
                Document doc = Jsoup.parse(url, 3000);
                //find book name title
                Elements title = doc.select("a[rel=mid_image]");
                String name = title.attr("title");
                //String link = title.attr("href");
                Log.d(TAG,"book name: " + name);
                //Log.d(TAG,"book link: " + link);
                Message msg = Message.obtain();
                msg.what = UPDATE_BOOK_NAME;
                msg.obj = name;
                mUIHandler.sendMessage(msg);

                Elements Eauthor = doc.select("a[rel=go_author]");
                String author = Eauthor.attr("title");
                Log.d(TAG,"book author: " + author);

                Message updateAuthor = Message.obtain();
                updateAuthor.what = UPDATE_BOOK_AUTHOR;
                updateAuthor.obj = author;
                mUIHandler.sendMessage(updateAuthor);

                Elements EPublish = doc.select("a[rel=mid_publish]");
                String publish = EPublish.attr("title");
                Log.d(TAG,"book publish: " + publish);

                Message updatePublish = Message.obtain();
                updatePublish.what = UPDATE_BOOK_PUBLISH;
                updatePublish.obj = publish;
                mUIHandler.sendMessage(updatePublish);


            } catch (MalformedURLException e) {
                e.printStackTrace();
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
                default:
                    break;
            }
        }
    };
}
