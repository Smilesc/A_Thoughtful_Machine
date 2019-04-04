package com.cita.a_thoughtful_machine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Select_Image extends AppCompatActivity {
    Button upload_button;
    Button analyze_button;
    ImageView imageView;
    Uri selectedImage;

    public final static int REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__image);

        upload_button = findViewById(R.id.upload_button);
        analyze_button = findViewById(R.id.analyze_button);
        imageView = findViewById(R.id.imageView);

        upload_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_CODE);
            }
        });

        analyze_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Intent intent = new Intent(Select_Image.this, LoadingScreen.class);
                intent.setData(selectedImage);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            System.out.println("IMAGE THINGS HERE: " + selectedImage);
            imageView.setImageURI(selectedImage);

            analyze_button.setEnabled(true);
        }
    }
}
