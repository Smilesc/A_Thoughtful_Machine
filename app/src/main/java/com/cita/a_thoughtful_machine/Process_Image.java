package com.cita.a_thoughtful_machine;

import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

class Process_Image extends AsyncTask<Void, Void, String> {

    private Exception exception;
    ProgressBar progressBar;

    public Process_Image(ProgressBar p){
        this.progressBar = p;
    }
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected String doInBackground(Void... urls) {
        try {
            URL url = new URL("https://accounts.google.com/o/oauth2/v2/auth?" +
                    "            scope=email%20profile&" +
                    "                    response_type=code&" +
                    "            redirect_uri=www.google.com" +
                    "                    client_id=1044392821881-63md27vb097bvljo0v01pq5bhvnju09m.apps.googleusercontent.com");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
//            urlConnection.setDoOutput(true);
//            urlConnection.setFixedLengthStreamingMode(572049);

//            urlConnection.addRequestProperty("client_id", your client id);
//            urlConnection.addRequestProperty("client_secret", your client secret);
//            urlConnection.setRequestProperty("Authorization", "OAuth " + token);
//

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        progressBar.setVisibility(View.GONE);
        Log.i("INFO", response);
//        responseView.setText(response);
    }
}

