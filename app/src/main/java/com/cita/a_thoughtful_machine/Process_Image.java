package com.cita.a_thoughtful_machine;

import android.accounts.AccountManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

class Process_Image extends AsyncTask<Void, Void, String> {

    private Exception exception;
    ProgressBar progressBar;
    String token;
    HttpURLConnection conn;
    Bitmap image;
    Bitmap grey_square;

    public Process_Image(ProgressBar p, String token, Bitmap image){
        this.progressBar = p;
        this.token = token;
        this.image = image;
    }
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected String doInBackground(Void... urls) {


        try {
            URL url = new URL("https://ml.googleapis.com/v1/projects/a-thoughtful-machine/models/atm_model1:predict?key=" + "AIzaSyB8FlL0vRUzKXtiqodjjAXp9p7m4xj6a60");
           conn = (HttpURLConnection) url.openConnection();
            //conn.setDoInput(true);
            conn.addRequestProperty("client_id", "1044392821881-63md27vb097bvljo0v01pq5bhvnju09m.apps.googleusercontent.com");
//            conn.addRequestProperty("client_secret", your client secret);
            conn.setRequestProperty("Authorization", "OAuth " + token);
            //conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

            //osw.write(R.string.image_body);
            osw.flush();
            osw.close();
            os.close();  //don't forget to close the OutputStream
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            return "WE DID IT";

        }
        catch(MalformedURLException mue){
            System.out.println("malformed url " + mue.getMessage());
        }
        catch(IOException io){
            System.out.println("IO exception " + io.getMessage());
        }
        return("There was an exception");
    }


    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        progressBar.setVisibility(View.GONE);
        Log.i("INFO", response);
        System.out.println("response: " + response);

    }



}

