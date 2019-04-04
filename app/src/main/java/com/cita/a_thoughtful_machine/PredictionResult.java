package com.cita.a_thoughtful_machine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PredictionResult extends AppCompatActivity {
    ImageView imageView;
    TextView prediction_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);
        prediction_result = findViewById(R.id.prediction_text);

        Intent intent = getIntent();
        ArrayList<String> prediction_array = intent.getExtras().getStringArrayList("prediction_array");

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

        String[] emotions = {"anger","anticipation","disagreeableness","disgust","fear","gratitude","happiness","humility","love","optimism","pessimism","regret","sadness","shyness","surprise","trust","neutral"};
        System.out.println("BOOL LIST");
        for(int x = 0; x<myBoolList.size(); x++){
            System.out.println(myBoolList.get(x));
        }
        for(int i = 0;i < myBoolList.size(); i++){
            if(myBoolList.get(i)){
                true_predictions.add(emotions[i]);
            }
        }

        System.out.println("TRUE PREDICTIONS");
        for(int x = 0; x<true_predictions.size(); x++){
            System.out.println(true_predictions.get(x));
        }

        StringBuilder emotion_list = new StringBuilder();
        for(int k = 0; k < true_predictions.size(); k++){
            emotion_list.append(true_predictions.get(k));
            if(k < true_predictions.size() - 1){
                emotion_list.append(", ");
            }
        }
        prediction_result.setText(emotion_list.toString());


    }
}
