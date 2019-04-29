package com.cita.a_thoughtful_machine;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class LoadingScreen extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    PredictionRequest predictionRequest;
    Uri selectedImage;
    int i_width;
    int i_height;
    Bitmap img_bitmap;
    JSONObject img_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        selectedImage = getIntent().getData();

        imageView.setImageURI(selectedImage);

        Bitmap cropped_image = cropImage(selectedImage);

        int[][] pixel_array = getRGBValues(cropped_image);

        try {
            JSONArray jsonArray = new JSONArray(pixel_array);
            img_data = new JSONObject();

            img_data.put("instances", jsonArray);
        } catch (org.json.JSONException je) {
            System.out.println(je.getMessage());
        }

        retrieveToken();

    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                // Get the result of the operation from the AccountManagerFuture.
                Bundle bundle = result.getResult();
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                System.out.println("TOKEN: " + token);
                predictionRequest = new PredictionRequest(progressBar, token, img_bitmap, img_data, getApplicationContext(), selectedImage);
                predictionRequest.execute();
            } catch (Exception e) {
                System.out.println("SOMETHING WENT WRONG: " + e.getMessage());
            }
            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
        }
    }

    private int[][] getRGBValues(Bitmap bitmap) {
        System.out.println("i_width before: " + i_width);
        System.out.println("i_height before: " + i_height);
        i_width = 200;
        i_height = 200;
        int[] img_argb = new int[40000];
        bitmap.getPixels(img_argb, 0, i_width, 0, 0, i_width, i_height);

        int[][] img_rgb = new int[1][120000];

        int i = 0;

        for (int y = 0; y < i_height; y++) {
            for (int x = 0; x < i_width; x++) {
                int index = y * i_width + x;

                int R = (img_argb[index] >> 16) & 0xff;     //bitwise shifting
                int G = (img_argb[index] >> 8) & 0xff;
                int B = img_argb[index] & 0xff;

                img_rgb[0][i] = R;
                img_rgb[0][i + 1] = G;
                img_rgb[0][i + 2] = B;
                i += 3;
            }

        }
        return img_rgb;
    }


    private Bitmap cropImage(Uri image_URI) {
        Bitmap cropped_image = createImage();

        try {
            img_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_URI);
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }

        float height = img_bitmap.getHeight();
        float width = img_bitmap.getWidth();

        float x_offset = 0;
        float y_offset = 0;

        boolean height_larger;

        if (height >= width) {
            height_larger = true;
            height = Math.round((200 / width) * height);
            width = 200;

            y_offset = (height - 200) / 2;

        } else {
            height_larger = false;
            width = Math.round((200 / height) * width);
            height = 200;

            x_offset = (width - 200) / 2;

        }

        i_width = (int) width;
        i_height = (int) height;

        int i_x_offset = (int) x_offset;
        int i_y_offset = (int) y_offset;

        Bitmap scaled_image = Bitmap.createScaledBitmap(img_bitmap, i_width, i_height, true);

        int cropped_img_x = 0;
        int cropped_img_y = 0;

        if(height_larger) {
            for (int y = i_y_offset; y < i_height - i_y_offset - 1; y++) {
                for (int x = 0; x < cropped_image.getWidth(); x++) {
                    cropped_image.setPixel(cropped_img_x, cropped_img_y,
                            scaled_image.getPixel(x, y));
                    cropped_img_x++;
                }
                cropped_img_x = 0;
                cropped_img_y++;
            }

        } else {
            for (int x = i_x_offset; x < i_width - i_x_offset - 1; x++) {
                for (int y = 0; y < cropped_image.getHeight(); y++) {
                    cropped_image.setPixel(cropped_img_x, cropped_img_y,
                            scaled_image.getPixel(x, y));
                    cropped_img_y++;
                }
                cropped_img_y = 0;
                cropped_img_x++;
            }

        }
        return cropped_image;
    }

    private Bitmap cropImageIntoBox(Uri image_URI) {
        Bitmap cropped_image = createImage();

        try {
            img_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_URI);
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }

        float height = img_bitmap.getHeight();
        float width = img_bitmap.getWidth();

        float x_offset = 0;
        float y_offset = 0;

        if (height >= width) {
            width = Math.round((200 / height) * width);
            height = 200;

            x_offset = (200 - width) / 2;
        } else {
            height = Math.round((200 / width) * height);
            width = 200;

            y_offset = (200 - height) / 2;
        }

        i_width = (int) width;
        i_height = (int) height;

        int i_x_offset = (int) x_offset;
        int i_y_offset = (int) y_offset;

        Bitmap scaled_image = Bitmap.createScaledBitmap(img_bitmap, i_width, i_height, true);

        int pix_x_si = 0;
        int pix_y_si = 0;
        for (int pix_x = i_x_offset; pix_x < i_width + i_x_offset; pix_x++) {
            for (int pix_y = i_y_offset; pix_y < i_height + i_y_offset; pix_y++) {
                cropped_image.setPixel(pix_x, pix_y,
                        scaled_image.getPixel(pix_x_si, pix_y_si));
                pix_y_si++;
            }
            pix_y_si = 0;
            pix_x_si++;
        }
        return cropped_image;
    }

    private void retrieveToken() {
        AccountManager am = AccountManager.get(this); // "this" references the current Context
        Account[] accounts = am.getAccountsByType("com.google");
        System.out.println("ACCOUNTS: " + accounts[0]);
        Account myAccount_ = accounts[0];
        Bundle options = new Bundle();

        am.getAuthToken(myAccount_,
                "oauth2:https://www.googleapis.com/auth/cloud-platform",
                options,
                true,
                new OnTokenAcquired(),
                new Handler(new OnError()));

    }

    public static Bitmap createImage() {
        int width = 200;
        int height = 200;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setARGB(255, 132, 132, 132);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

}
