package com.jonathan.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
