package com.jonathan.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jonathan.bookshelf.bookcase.SearchActivity;

public class MainPageActivity extends AppCompatActivity {

    private Context mContext;

    private Button mAddBtn, mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mContext = this;
        init_view();


    }

    private void init_view(){
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookInfoActivity.class);
                startActivity(intent);

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
}
