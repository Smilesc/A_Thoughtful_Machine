package com.cita.a_thoughtful_machine;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

public class LoadingScreen extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    static final int RC_SIGN_IN = 6;
    Button sign_in;
    Process_Image process_image;

    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        imageView = findViewById(R.id.imageView);
        Uri selectedImage = getIntent().getData();
        imageView.setImageURI(selectedImage);
        Bitmap grey_square = createImage();

        imageView.setImageBitmap(grey_square);


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
                System.out.println("PIX_Y_SI: " + pix_y_si + " PIX_Y: " + pix_y);
                grey_square.setPixel(pix_x, pix_y,
                        scaled_image.getPixel(pix_x_si, pix_y_si));
                pix_y_si++;
            }
            pix_y_si = 0;
            pix_x_si++;
        }

        imageView.setImageBitmap(grey_square);
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
                process_image = new Process_Image(progressBar, token, image);
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
        paint.setARGB(100, 132,132,132);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

}
