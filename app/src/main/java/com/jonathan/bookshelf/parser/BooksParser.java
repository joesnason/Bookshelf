package com.jonathan.bookshelf.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jonathan.bookshelf.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jonathan on 2017/2/16.
 */

public class BooksParser {

    private String mISBN;
    private URL mUrl;
    private Document mDoc;

    public BooksParser(String isbn) throws IOException {

        mISBN = isbn;
        mUrl = new URL(Config.QUERY_URL + mISBN);
        mDoc = Jsoup.parse(mUrl, 3000);

    }

    public String getBookName(){
        Elements title = mDoc.select("a[rel=mid_image]");
        return title.attr("title");

    }

    public String getBookAuthor(){
        Elements Eauthor = mDoc.select("a[rel=go_author]");
        return Eauthor.attr("title");

    }

    public String getBookPublish(){
        Elements EPublish = mDoc.select("a[rel=mid_publish]");
        return EPublish.attr("title");
    }


    public String getBookCoverLink(){
        Elements EImg = mDoc.select("img[class=itemcov]");
        return EImg.attr("data-original");
    }

    public Bitmap getBookCover() throws IOException {
        String image_url = getBookCoverLink();
        InputStream in = new URL(image_url).openStream();
        return BitmapFactory.decodeStream(in);
    }

}
