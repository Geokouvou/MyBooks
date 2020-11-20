package com.example.mybooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;
import java.util.ListIterator;

public class MyBookList extends AppCompatActivity {


    LinearLayout linearLayout;
    private static Gson gson = new Gson();
    private TypeToken<List<Book>> token = new TypeToken<List<Book>>() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_list);



        JsonArrayRequest arrayRequest=new JsonArrayRequest(
                Request.Method.GET, MainActivity.host+"api/books/mybooks", null,
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
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                2*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d("request{}", "1");
        MainActivity.getRequestQueue().add(arrayRequest);
        Log.d("request()", "2");


    }

    private void showBooks(JSONArray array) {
        //gui code
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout2);
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

            Book book = bookIter.next();

            textView.setTextColor(Color.BLUE);

            String title = book.getTitle();
            textView.setText(title);
            textView.setTag(books.get(count - 1).getId());
            textView.setId(count);
            textView.setClickable(true);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String text = ((TextView) v).getText().toString();

                    Intent intent = new Intent(MyBookList.this, BookDetails.class);
                    intent.putExtra(MainActivity.TITLE, (long) v.getTag());
                    startActivity(intent);


                }
            });
            linearLayout.addView(textView);


        }
    }
}
