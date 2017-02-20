package com.jonathan.bookshelf.databases;

/**
 * Created by Jonathan on 2017/2/17.
 */

public class Book {

    private long id = 0;
    private String name = null;
    private String ISBN = null;
    private String author = null;
    private String publish = null;
    private String coverLink = null;

    public Book() {

    }

    public Book(String bookISBN) {
        ISBN = bookISBN;
    }

    public void setID(long bookid){
        id = bookid;
    }

    public long getID(){
        return id;
    }

    public void setName(String bookname){
        name = bookname;
    }

    public String getName(){
        return name;
    }

    public void setISBN(String bookISBN){
        ISBN = bookISBN;
    }

    public String getISBN(){
        return ISBN;
    }

    public void setAuthor(String bookauthor){
        author = bookauthor;
    }

    public String getAuthor(){
        return author;
    }

    public void setPublish(String bookpublish){
        publish = bookpublish;
    }

    public String getPublish(){
        return publish;
    }

    public void setCoverLink(String bookLink){
        coverLink = bookLink;
    }

    public String getCoverLink(){
        return coverLink;
    }

}
