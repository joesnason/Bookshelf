package com.jonathan.bookshelf;

import android.os.Environment;

import java.io.File;

/**
 * Created by Jonathan on 2017/2/15.
 */

public class Config {
    public static String  QUERY_URL = "http://search.books.com.tw/search/query/key/";
    public static final String FOLDER_NAME = "Boookshelf";
    public static final File FOLDER = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES + "/" + FOLDER_NAME);

}
