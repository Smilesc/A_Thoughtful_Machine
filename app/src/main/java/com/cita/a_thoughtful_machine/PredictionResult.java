package com.cita.a_thoughtful_machine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PredictionResult extends AppCompatActivity {
    ImageView imageView;
    TextView prediction_result;
    Uri img_URI;
    Button try_another;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);
        prediction_result = findViewById(R.id.prediction_text);
        imageView = findViewById(R.id.imageView);
        try_another = findViewById(R.id.try_another);

        Intent intent = getIntent();
        ArrayList<String> prediction_array = intent.getExtras().getStringArrayList("prediction_array");
        img_URI = intent.getParcelableExtra("uri");

        imageView.setImageURI(img_URI);
        List<Boolean> myBoolList = new ArrayList<>();
        prediction_array.set(0, prediction_array.get(0).substring(18, 21));
        prediction_array.set(16, prediction_array.get(16).substring(0, 4));
        for (int j = 0; j < prediction_array.size(); j++) {
            if (prediction_array.get(j).equals("0.0") || prediction_array.get(j).equals(" 0.0")) {
                myBoolList.add(false);
            } else {
                myBoolList.add(true);
            }
        }

        List<String> true_predictions = new ArrayList<>();

        String[] emotions = {"anger", "anticipation", "disagreeableness", "disgust", "fear",
                "gratitude", "happiness", "humility", "love", "optimism", "pessimism", "regret",
                "sadness", "shyness", "surprise", "trust", "neutral"};

        for (int i = 0; i < myBoolList.size(); i++) {
            if (myBoolList.get(i)) {
                true_predictions.add(emotions[i]);
            }
        }


        StringBuilder emotion_list = new StringBuilder();
        if (true_predictions.size() == 1) {
            emotion_list.append(true_predictions.get(0));
        } else if (true_predictions.size() == 2) {
            emotion_list.append(true_predictions.get(0));
            emotion_list.append(getResources().getString(R.string.space));
            emotion_list.append(getResources().getString(R.string.and));
            emotion_list.append(true_predictions.get(1));
        } else {
            for (int k = 0; k < true_predictions.size() - 1; k++) {
                emotion_list.append(true_predictions.get(k));
                emotion_list.append(getResources().getString(R.string.comma));
            }
            emotion_list.append(getResources().getString(R.string.and));
            emotion_list.append(true_predictions.get(true_predictions.size() - 1));
        }
        prediction_result.setText(getResources().getString(R.string.prediction_text, emotion_list.toString()));

        try_another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Select_Image.class);
                startActivity(intent);
            }
        });
    }
}
