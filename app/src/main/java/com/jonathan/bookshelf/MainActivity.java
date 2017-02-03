package com.jonathan.bookshelf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Bookshelf";
    private Activity mMainActivity;
    private Button scan_btn;
    private TextView bookID;


    static private int  REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mMainActivity = this;

        init_view();
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
            bookID.setText(scanContent);
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
        bookID = (TextView) findViewById(R.id.bookid);
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
}
