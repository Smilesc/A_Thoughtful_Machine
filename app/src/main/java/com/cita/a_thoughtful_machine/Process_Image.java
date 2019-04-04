package com.cita.a_thoughtful_machine;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
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
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class Process_Image extends AsyncTask<Void, Void, String> {

    private Exception exception;
    ProgressBar progressBar;
    String token;
    HttpURLConnection conn;
    Bitmap image;
    Bitmap cropped_image;
    JSONObject obj;
    Context context;

    public Process_Image(ProgressBar p, String token, Bitmap image, JSONObject obj, Context context) {
        this.progressBar = p;
        this.token = token;
        this.image = image;
        this.obj = obj;
        this.context = context;
    }

    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected String doInBackground(Void... urls) {


        try {
            URL url = new URL("https://ml.googleapis.com/v1/projects/a-thoughtful-machine/models/atm_model1:predict?key=");
            conn = (HttpURLConnection) url.openConnection();
            //conn.setDoInput(true);
            conn.addRequestProperty("client_id", "1044392821881-63md27vb097bvljo0v01pq5bhvnju09m.apps.googleusercontent.com");
//            conn.addRequestProperty("client_secret", your client secret);
            conn.setRequestProperty("Authorization", "OAuth " + token);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(obj.toString());
            osw.flush();
            osw.close();
            os.close();  //don't forget to close the OutputStream
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String input = in.readLine();
            System.out.println("PREDICTIONS: " + input);
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(input.split(",")));

            Intent i = new Intent(context, PredictionResult.class);
            i.putStringArrayListExtra("prediction_array", myList);
            context.startActivity(i);


            return "Executed";

        } catch (MalformedURLException mue) {
            System.out.println("malformed url " + mue.getMessage());
        } catch (IOException io) {
            System.out.println("IO exception " + io.getMessage());
        }
        return ("There was an exception");
    }


    protected void onPostExecute(String response) {
        if (response == null) {
            response = "THERE WAS AN ERROR";
        }
        progressBar.setVisibility(View.GONE);
        Log.i("INFO", response);

    }


}

