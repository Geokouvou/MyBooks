package com.example.mybooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static Gson gson = new Gson();
    private TypeToken<List<User>> token = new TypeToken<List<User>>() {
    };


    // public static final String USERNAME = "com.example.ergasia.USERNAME";
    public static final String PASSWORD = "com.example.ergasia.PASSWORD";
    //public static String pass="123";
    //private SharedPreferences preferences;
    public static long userId;

    private TextView textView3;
    private EditText passwordView;
    private EditText usernameView;
    private EditText confirmPasswordView;
    private Button enterBtn;
    private TextView registerTextView;
    private boolean registerMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //startActivity(new Intent(this, MainActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView3 = (TextView)findViewById(R.id.textView3);
        passwordView = (EditText)findViewById(R.id.password);
        usernameView = (EditText)findViewById(R.id.username);
        confirmPasswordView = (EditText)findViewById(R.id.confirmPassword);
        enterBtn = (Button)findViewById(R.id.button2);
        registerTextView = (TextView)findViewById(R.id.registerBtn);

        MainActivity.requestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()){
            @Override
            public NetworkResponse performRequest(Request<?> request) throws VolleyError {
                NetworkResponse nr = super.performRequest(request);
                Log.d("circuit", "performRequest()");
                MyRetryPolicy.clearFailures();
                return nr;
            }
        });
        MainActivity.requestQueue.start();

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault( manager ) ;



        textView3.setText("Please type your username and password...");
        confirmPasswordView.setVisibility(View.INVISIBLE);
        enterBtn.setText("Login");

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("My app","This is a message");
                String password= passwordView.getText().toString();
                String username = usernameView.getText().toString();

                if(registerMode){
                    //register(username, password);
                }else{
                    login(username, password);
                }



            }
        });

    }

    public void toggleMode(View view){
        registerMode = !registerMode;
        if(registerMode){
            textView3.setText("Please type your chosen username, password and password confirmation...");
            confirmPasswordView.setVisibility(View.VISIBLE);
            enterBtn.setText("Register");
        }else{
            textView3.setText("Please type your username, password ...");
            confirmPasswordView.setVisibility(View.INVISIBLE);
            enterBtn.setText("Login");
        }
    }

    public void login(String username ,String password) {
        //String s = preferences.getString("pass",null);
        JSONObject jsonBody = null;
        try{
            jsonBody = new JSONObject().put("username", username).put("password", password);
        }catch (JSONException ex){ throw new RuntimeException(ex); }
        final JsonObjectRequest userRequest = new JsonObjectRequest(
                Request.Method.POST, MainActivity.host+"api/users/login", jsonBody, //"\""+username+":"+password+"\""
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("onResponse()", response.toString());
                        // {"success": true}

                        try {
                            if(response.getBoolean("success")) {
                                userId =response.getLong("userId");
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            }else{

                                Toast.makeText(getApplicationContext(), "Invalid credentials! " , Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException ex){}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        error.printStackTrace();
                        Log.d("onErrorResponse()", error.toString());
                        if(networkResponse != null){
                            Log.d("onErrorResponse()", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                        }
                        Toast.makeText(getApplicationContext(), "Something went wrong! " , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        MainActivity.addRequestToQueue(userRequest);


    }

               public void register(JSONObject userJson){
                User user = gson.fromJson(userJson.toString(), User.class);


                if (user.getUserId()==null) {//register
                    textView3.setText("Create username and password");
                    confirmPasswordView.setVisibility(View.VISIBLE);
                    JsonObjectRequest detailsRequest = new JsonObjectRequest(
                    Request.Method.POST, MainActivity.host+"api/user/register/" + user.getUserId(), null, //TODO//"{username: \"123\", password:123}",
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("onResponse()", response.toString());
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



                    } else {//login




        }

    }


}
