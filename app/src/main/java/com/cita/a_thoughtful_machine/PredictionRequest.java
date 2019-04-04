package com.cita.a_thoughtful_machine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

class PredictionRequest extends AsyncTask<Void, Void, String> {

    private Exception exception;
    ProgressBar progressBar;
    String token;
    HttpURLConnection conn;
    Bitmap img_bitmap;
    JSONObject img_data;
    Context context;
    Uri img_URI;

    public PredictionRequest(ProgressBar p, String token, Bitmap img_bitmap, JSONObject img_data, Context context, Uri imgURI) {
        this.progressBar = p;
        this.token = token;
        this.img_bitmap = img_bitmap;
        this.img_data = img_data;
        this.context = context;
        this.img_URI = imgURI;
    }

    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected String doInBackground(Void... urls) {


        try {
            URL url = new URL("https://ml.googleapis.com/v1/projects/a-thoughtful-machine/models/atm_model1:predict?key=");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("client_id", "1044392821881-63md27vb097bvljo0v01pq5bhvnju09m.apps.googleusercontent.com");
            conn.setRequestProperty("Authorization", "OAuth " + token);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

            osw.write(img_data.toString());
            osw.flush();
            osw.close();
            os.close();
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String input = in.readLine();
            System.out.println("PREDICTIONS: " + input);
            ArrayList<String> myList = new ArrayList<>(Arrays.asList(input.split(",")));

            Intent i = new Intent(context, PredictionResult.class);
            i.putStringArrayListExtra("prediction_array", myList);
            i.putExtra("uri", img_URI);
            context.startActivity(i);

            return "Request returned";

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

