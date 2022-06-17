package com.example.creolise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.creolise.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Dico;

public class Vocal extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static Pattern pattern;
    private static Matcher matcher;
    private Map<String, String> dictionary = new HashMap<String, String>();
    String[] ListElements = new String[]{
    };




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //création du dico de données
        dictionary.put("rougail", "Plat traditionnel créole, attention recette à ne pas revisiter)");
        dictionary.put("pilon", "Mortier (Instrument cylindrique servant à piler.)");
        dictionary.put("un Malbar", "À la Réunion, Indien, Indienne non musulman.");
        dictionary.put("mon cabri", "chèvre ou bouc");
        dictionary.put("mon loto", "voiture");
        dictionary.put("mon tantine", "Expression qui désigne les filles de la Réunion");
        dictionary.put("la case", "Maison");



    }

    public void ActionMicro(View v) {

            Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            startActivityForResult(speechRecognizerIntent,202);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"rcf-Latn-RE");
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ListView listview = findViewById(R.id.list_dico);

        final List<String> ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, ListElementsArrayList);

        listview.setAdapter(adapter);

        if (requestCode== 202){
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Log.d("micro","-"+results.get(0));
            //recup le mot puis boucle sur les key du dico
            pattern = Pattern.compile(results.get(0));
            Iterator iter = dictionary.entrySet().iterator();
            String err = "Expression non trouvé, veuillez réessayer";

            while (iter.hasNext()) {
                Map.Entry mEntry = (Map.Entry) iter.next();
                String key = (String) mEntry.getKey();
                matcher = pattern.matcher(key);

                //si match on traduis le mot sinon on envoie une popup erreur
                if (matcher.find()) {
                    String value = (String) (mEntry.getValue());
                    String def = key + ":  " + value;
                    ListElementsArrayList.add(def);
                    break;

                }

                  //  Toast.makeText(this,"Expression impossible de traduire", Toast.LENGTH_LONG).show();



            }
            adapter.notifyDataSetChanged();
        }
    }

    /*ListView listview = findViewById(R.id.list_dico);
        Button btnAdd = this.findViewById(R.id.vocal);









        btnAdd.setOnClickListener(view -> {

            Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            startActivityForResult(speechRecognizerIntent,9000);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"rcf-Latn-RE");
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            Log.d("clickActionIntent","Je suis dans le clickActionIntent");





        });




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode== 9000){
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            TextView zone = this.findViewById(R.id.mot_creole);
            TextView traduction = this.findViewById(R.id.traduction);
            //recup le mot du vocal et affiche dans un UI
            zone.append(results.get(0));



            //recup le mot puis boucle sur les key du dico
            pattern = Pattern.compile(results.get(0));
            Iterator iter = dictionary.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry mEntry = (Map.Entry) iter.next();
                String key = (String) mEntry.getKey();
                matcher = pattern.matcher(key);

                //si match on traduis le mot sinon on envoie une popup erreur
                if (matcher.find()){
                    String value = (String) (mEntry.getValue());



                    traduction.append(value);

                    String def = key + ":    "+value;


                }

            }





        }
    }

}*/




}