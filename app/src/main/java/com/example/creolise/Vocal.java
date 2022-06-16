package com.example.creolise;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.creolise.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vocal extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static Pattern pattern;
    private static Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button btnAdd = this.findViewById(R.id.vocal);
        btnAdd.setOnClickListener(view -> {

            Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            startActivityForResult(speechRecognizerIntent,9000);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"rcf-Latn-RE");
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            Log.d("clickActionIntent","Je suis dans le clickActionIntent");

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 9000){
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            TextView zone = this.findViewById(R.id.mot_creole);
            TextView traduction = this.findViewById(R.id.traduction);
            zone.append(results.get(0));

            pattern = Pattern.compile(results.get(0));
            matcher = pattern.matcher("rougail");
            while(matcher.find()) {
                traduction.append("Plat traditionnel créole, attention recette à ne pas revisiter)");

            }
            matcher = pattern.matcher("pilon");
            while(matcher.find()) {
                traduction.append("Mortier (Instrument cylindrique servant à piler.)");

            }
        }
    }

}

