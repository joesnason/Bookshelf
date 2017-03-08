package com.jonathan.bookshelf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jonathan.bookshelf.bookcase.SearchActivity;
import com.jonathan.bookshelf.databases.Book;

public class MainPageActivity extends AppCompatActivity {

    static private String TAG = "Bookshelf_Main";
    static private int  REQUEST_CAMERA = 1;
    private Context mContext;

    private Button mAddBtn, mSearchBtn;
    private Activity mMainActivity;
    private Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mMainActivity = this;
        mContext = this;
        init_view();


        // Check if the Camera permission is already available.
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // already available
            mAddBtn.setEnabled(true);
        } else {
            mAddBtn.setEnabled(false);
            // asking whether to allow permission...
            requestCameraPermission();
        }

    }

    private void init_view(){
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(mMainActivity);
                scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                scanIntegrator.initiateScan();

            }
        });

        mSearchBtn = (Button) findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });

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
                mAddBtn.setEnabled(true);
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                mAddBtn.setEnabled(false);
            }
        }
        // other callback
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if (scanContent == null) {
                return;
            }

            Intent BookinfoIntent = new Intent(mContext, BookInfoActivity.class);
            BookinfoIntent.putExtra("ISBN", scanContent);
            startActivity(BookinfoIntent);
        }
    }
}
