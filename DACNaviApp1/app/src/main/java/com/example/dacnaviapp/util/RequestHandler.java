package com.example.dacnaviapp.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dacnaviapp.ConverterActivity;
import com.example.dacnaviapp.common.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class RequestHandler {

    public static final String TAG = "RequestHandler";

    public static String getDAResponse(String inputStr) {
        try {
            Log.i(TAG, "REQUESTED JSON STRING - " + inputStr);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new URI(Constants.NETWORK.HOST_URL + "/test/searchda"));
            // post.setEntity(new StringEntity(location));
            StringEntity input = new StringEntity(inputStr);
            input.setContentType("application/json");
            post.setEntity(input);

            Log.d(TAG, "REQUEST URL - " + post.getURI().toString());
            Log.d(TAG, "JSON - " + post.getEntity().getContent());

            HttpUriRequest request = post;
            Log.d(TAG, "Request created successfully.");
            HttpResponse serverResponse = client.execute(request);
            Log.d(TAG, "Request sent successfully.");
            BasicResponseHandler handler = new BasicResponseHandler();
            Log.d(TAG, "Response handler created.");

            return handler.handleResponse(serverResponse);
        } catch (Exception e) {
            Log.e(TAG, "Error while processing request: " + e.getMessage());
        }

        return "";
    }

    public static String getMultipleDAResponse(String inputStr) {
        try {
            Log.i(TAG, "REQUESTED JSON STRING - " + inputStr);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new URI(Constants.NETWORK.HOST_URL + "/test/searchMultipleDA"));
            // post.setEntity(new StringEntity(location));
            StringEntity input = new StringEntity(inputStr);
            input.setContentType("application/json");
            post.setEntity(input);

            Log.d(TAG, "REQUEST URL - " + post.getURI().toString());
            Log.d(TAG, "JSON - " + post.getEntity().getContent());

            HttpUriRequest request = post;
            Log.d(TAG, "Request created successfully.");
            HttpResponse serverResponse = client.execute(request);
            Log.d(TAG, "Request sent successfully.");
            BasicResponseHandler handler = new BasicResponseHandler();
            Log.d(TAG, "Response handler created.");

            return handler.handleResponse(serverResponse);
        } catch (Exception e) {
            Log.e(TAG, "Error while processing request: " + e.getMessage());
        }

        return "";
    }

    public static String getRegisterDAResponse(String inputStr) {
        try {
            Log.i(TAG, "REQUESTED JSON STRING - " + inputStr);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new URI(Constants.NETWORK.HOST_URL + "/test/registerda"));
            // post.setEntity(new StringEntity(location));
            StringEntity input = new StringEntity(inputStr);
            input.setContentType("application/json");
            post.setEntity(input);

            Log.d(TAG, "REQUEST URL - " + post.getURI().toString());
            Log.d(TAG, "JSON - " + post.getEntity().getContent());

            HttpUriRequest request = post;
            Log.d(TAG, "Request created successfully.");
            HttpResponse serverResponse = client.execute(request);
            Log.d(TAG, "Request sent successfully.");
            BasicResponseHandler handler = new BasicResponseHandler();
            Log.d(TAG, "Response handler created.");

            return handler.handleResponse(serverResponse);
        } catch (Exception e) {
            Log.e(TAG, "Error while processing request: " + e.getMessage());
        }

        return "";
    }

}
