package com.cita.a_thoughtful_machine;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingScreen extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    static final int RC_SIGN_IN = 6;
    Button sign_in;
    Process_Image process_image;

    Bitmap image;
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        imageView = findViewById(R.id.imageView);
        Uri selectedImage = getIntent().getData();
        imageView.setImageURI(selectedImage);
        Bitmap cropped_image = createImage();

        imageView.setImageBitmap(cropped_image);


        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        }
        catch(IOException io){
            System.out.println(io.getMessage());
        }

        float height = image.getHeight();
        float width = image.getWidth();

        float x_offset = 0;
        float y_offset = 0;

        if(height >= width){
            width = Math.round((200/height)*width);
            height = 200;

            x_offset = (200 - width)/2;
        }
        else{
            height = Math.round((200/width)*height);
            width = 200;

            y_offset = (200 - height)/2;
        }

        int i_width = (int) width;
        int i_height = (int) height;

        int i_x_offset = (int) x_offset;
        int i_y_offset = (int) y_offset;

        Bitmap scaled_image = Bitmap.createScaledBitmap(image, i_width, i_height, true);

        imageView.setImageBitmap(scaled_image);
        int pix_x_si = 0;
        int pix_y_si = 0;
        for(int pix_x = i_x_offset; pix_x < i_width + i_x_offset; pix_x++){
            for(int pix_y = i_y_offset; pix_y < i_height + i_y_offset; pix_y++){
                cropped_image.setPixel(pix_x, pix_y,
                        scaled_image.getPixel(pix_x_si, pix_y_si));
                pix_y_si++;
            }
            pix_y_si = 0;
            pix_x_si++;
        }

        imageView.setImageBitmap(cropped_image);

        int[] img_argb = new int[40000];
        cropped_image.getPixels(img_argb,0,i_width,0,0,i_width,i_height);

        List<Integer> img_rgb = new ArrayList<>();

        int[][] img_test = new int[1][120000];

        int i = 0;

        for (int y = 0; y < i_height; y++) {
            for (int x = 0; x < i_width; x++) {
                int index = y * i_width + x;

                int R = (img_argb[index] >> 16) & 0xff;     //bitwise shifting
                int G = (img_argb[index] >> 8) & 0xff;
                int B = img_argb[index] & 0xff;

                img_test[0][i] = R;
                img_test[0][i + 1] = G;
                img_test[0][i + 2] = B;
                i+=3;

                img_rgb.add(R);
                img_rgb.add(G);
                img_rgb.add(B);
            }

        }

        try {
            JSONArray jsonArray = new JSONArray(img_test);
            obj = new JSONObject();

            obj.put("instances", jsonArray);
        }

        catch(org.json.JSONException je){
            System.out.println(je.getMessage());
        }
        sign_in = findViewById(R.id.sign_in);

        progressBar = findViewById(R.id.progressBar);

        AccountManager am = AccountManager.get(this); // "this" references the current Context
        Account[] accounts = am.getAccountsByType("com.google");
        System.out.println("ACCOUNTS: " + accounts[0]);
        Account myAccount_ = accounts[0];
        Bundle options = new Bundle();

        am.getAuthToken(
                myAccount_,                     // Account retrieved using getAccountsByType()
                "oauth2:https://www.googleapis.com/auth/cloud-platform",            // Auth scope
                options,                        // Authenticator-specific options
                true,                           // Your activity
                new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                new Handler(new OnError()));    // Callback called if an error occurs

    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                // Get the result of the operation from the AccountManagerFuture.
                Bundle bundle = result.getResult();
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                System.out.println("TOKEN: " + token);
                process_image = new Process_Image(progressBar, token, image, obj);
                process_image.execute();
            }
            catch(Exception e){
                System.out.println("SOMETHING WENT WRONG: " + e.getMessage());
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static Bitmap createImage() {
        int width = 200;
        int height = 200;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setARGB(255, 132,132,132);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

}
