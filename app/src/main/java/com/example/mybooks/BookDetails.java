package com.example.mybooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.List;
import java.util.ListIterator;

public class BookDetails extends AppCompatActivity {

    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    Button borrowBtn;
    Button returnBtn;

    public static final String TITLE = "com.example.ergasia.TITLE";
    public static final String AUTHOR = "com.example.ergasia.AUTHOR";
    //static RequestQueue requestQueue = MainActivity.requestQueue;
    private long bookId;


    private static Gson gson = new Gson();
    private TypeToken<List<Book>> token = new TypeToken<List<Book>>() {
    };


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.newPass2);
        editText1.setEnabled(false);
        editText2.setEnabled(false);
        editText3.setEnabled(false);
        editText4.setEnabled(false);

        borrowBtn = (Button) findViewById(R.id.borrowBtn);
        returnBtn = (Button) findViewById(R.id.returnBtn);
        Intent intent = getIntent();
        bookId = intent.getLongExtra(MainActivity.TITLE, -1);
        Log.d("GOT INTENT", bookId + "");

        JsonObjectRequest detailsRequest = new JsonObjectRequest(
                Request.Method.GET, MainActivity.host+"api/books/" + bookId, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("onResponse()", response.toString());
                        showDetails(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("onErrorResponse()", error.toString());
                    }
                }
        );
        detailsRequest.setRetryPolicy(new DefaultRetryPolicy(
                2 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainActivity.addRequestToQueue(detailsRequest);

    }


    public void displayBook(Book b) {
        Log.d("displayBook", "setText");
        editText1.setText(b.getTitle());
        editText2.setText(b.getAuthor());
        editText3.setText(b.getDateOfIssue());
        editText4.setText(b.getAvailable().toString());
        //oldTitle=title;


    }

    public void borrow(View view){
        Log.d("borrow()", "bookId="+bookId);
        borrowBtn.setVisibility(View.INVISIBLE);
        borrowBtn.setEnabled(false);
        borrowBtn.setClickable(false);
        borrow(bookId);
    }

    private void borrow(long bookId){
        Log.d("borrow()", "2");


        //POST api/books/borrow/5
        JsonObjectRequest detailsRequest = new JsonObjectRequest(
                Request.Method.PUT, MainActivity.host+"api/books/borrow/" + bookId,null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("onResponse()", response.toString());
                        Intent intent = new Intent(BookDetails.this, MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("onErrorResponse()", error.toString());
                    }
                }
        );
        MainActivity.addRequestToQueue(detailsRequest);
    }
    public void returnBook(View view){
        returnBtn.setVisibility(View.INVISIBLE);
        returnBtn.setEnabled(false);
        returnBtn.setClickable(false);
        returnBook(bookId);
    }
    public void returnBook(long id){
        JsonObjectRequest detailsRequest = new JsonObjectRequest(
                Request.Method.PUT, MainActivity.host+"api/books/return/" + bookId,null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("onResponse()", response.toString());
                        Intent intent = new Intent(BookDetails.this, MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("onErrorResponse()", error.toString());
                    }
                }
        );
        MainActivity.addRequestToQueue(detailsRequest);

    }


    public void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    private void showDetails(JSONObject bookJson) {
        Log.d("showDetails", "book");
        Book book = gson.fromJson(bookJson.toString(), Book.class);


        if (book.getAvailable()) {
            returnBtn.setVisibility(View.INVISIBLE);
            returnBtn.setEnabled(false);
            returnBtn.setClickable(false);
        } else {
            long userId=Login.userId;
            if(!(userId==book.getBorrower())){
                returnBtn.setVisibility(View.INVISIBLE);
                returnBtn.setEnabled(false);
                returnBtn.setClickable(false);
            }

            borrowBtn.setVisibility(View.INVISIBLE);
            borrowBtn.setEnabled(false);
            borrowBtn.setClickable(false);
        }

        displayBook(book);
    }



}

