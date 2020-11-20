package com.example.mybooks;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

public class MyRetryPolicy extends DefaultRetryPolicy {
    private static  int failures = 0;
    /*@Override
    public void postResponse(Request<?> request, Response<?> response){

    }
     */
    MyRetryPolicy(){

    }

    MyRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier){
        super(initialTimeoutMs, maxNumRetries, backoffMultiplier);
    }

    /**
     * Parses a response from the network or cache and delivers it. The provided Runnable will be
     * executed after delivery.

    @Override
    public void postResponse(Request<?> request, Response<?> response, Runnable runnable){};


    @Override
    public void postError(Request<?> request, VolleyError error){};
*/

    @Override
    public void retry(VolleyError error) throws VolleyError {
        failures++;
        Log.d("circuit", "retry() "+failures);
        super.retry(error);
    }

    public static void clearFailures(){
        Log.d("circuit", "clearFailures()");
        failures=0;
    }
    public static boolean thresholdExceeded(){
        Log.d("circuit", "thresholdExceeded() "+failures);
        return failures > 5;

    }
}
