package com.example.mybooks;

import java.util.Date;

public class Book {


    private long id;
   private String title;
    private String author;
    private String dateOfIssue;

    public long getBorrower() {
        return borrower;
    }

    public void setBorrower(long borrower) {
        this.borrower = borrower;
    }

    private long borrower;
    private Boolean available;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
