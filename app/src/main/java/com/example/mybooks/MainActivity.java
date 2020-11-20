package com.example.mybooks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;


import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    public static final String host = "http://192.168.1.4:58064/";

    static RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    public static RequestQueue getRequestQueue(){
        if(requestQueue == null){
        }
        return requestQueue;
    }
    private static boolean circuitClosed = true;
    private static long lastBreak = 0;
    private static long trialSentFor = 0;
    synchronized public static void addRequestToQueue(JsonRequest req){
        req.setShouldRetryServerErrors(true);
        req.setRetryPolicy(new MyRetryPolicy(
                2 * 1000,
                3,
                0));
        if(MyRetryPolicy.thresholdExceeded()){
            if(circuitClosed) {
                circuitClosed = false;
                lastBreak = System.currentTimeMillis();
            }
        }else{
            circuitClosed = true;
        }
        Log.d("addRequestToQueue()", "circuitClosed="+circuitClosed);
        Log.d("addRequestToQueue()", "timePassed="+(System.currentTimeMillis()-lastBreak));
        if(circuitClosed){ //CLOSED
            getRequestQueue().add(req);
        }else{
            if(System.currentTimeMillis()-lastBreak > 30*1000){ // Half-Open
                if(trialSentFor < lastBreak) {
                    getRequestQueue().add(req); //send trial
                    trialSentFor = lastBreak;
                    lastBreak = System.currentTimeMillis();
                }
            }else{ // OPEN
                req.getErrorListener().onErrorResponse( new VolleyError("Circuit open!"));
            }
        }
    }


    LinearLayout linearLayout;
    public static final String TITLE = "com.example.ergasia.SHOWTITLE";
    public static final String ME = "com.example.ergasia.SHOWMYID";

    private static Gson gson = new Gson();
    private TypeToken<List<Book>> token = new TypeToken<List<Book>>() {
    };

    private void showBooks(JSONArray array) {
        //gui code
        linearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
        linearLayout.removeAllViews();


//JSONObject
        final List<Book> books = gson.fromJson(array.toString(), token.getType());
        //book.setAuthor(obj.getString("author"));

        ListIterator<Book> bookIter = books.listIterator();
        int count = 0;
        while (bookIter.hasNext()) {
            count++;
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 80));
            textView.setGravity(Gravity.CENTER_VERTICAL);

            Book book= bookIter.next();

            if(book.getAvailable()){
                textView.setTextColor(Color.GREEN);
            }else{
                textView.setTextColor(Color.RED);
            }


            String title= book.getTitle();
            textView.setText(title);
            textView.setTag(books.get(count-1).getId());
            textView.setId(count);
            textView.setClickable(true);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String text = ((TextView) v).getText().toString();

                    Intent intent = new Intent(MainActivity.this, BookDetails.class);
                    intent.putExtra(MainActivity.TITLE, (long)v.getTag());
                    startActivity(intent);


                }
            });
            linearLayout.addView(textView);


        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        JsonArrayRequest arrayRequest=new JsonArrayRequest(
                Request.Method.GET, MainActivity.host+"api/books", null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("onResponse()", response.toString());
                        showBooks(response);
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

        Log.d("request{}", "1");

        MainActivity.addRequestToQueue(arrayRequest);



    }

    public void myBooks(View view){
        Intent intent = new Intent(MainActivity.this, MyBookList.class);
        //intent.putExtra(MainActivity.ME, userId);
        startActivity(intent);


    }
    public void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
    }
}
