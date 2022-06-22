package com.example.creolise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.creolise.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Dico;
import utils.OpenHelpher;

public class Vocal extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static Pattern pattern;
    private static Matcher matcher;
    private Map<String, String> dictionary = new HashMap<String, String>();
    private OpenHelpher openHelper;
    private LocationManager locationManage;

    ArrayList<String> ar = new ArrayList<String>();
    String[] ListElements = new String[] {
    };




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.creolise);

        //création du dico de données
        dictionary.put("rougail", "Plat traditionnel créole (attention recette à ne pas revisiter)");
        dictionary.put("pilon", "Mortier (Instrument cylindrique servant à piler.)");
        dictionary.put("un Malbar", "À la Réunion, Indien, Indienne non musulman.");
        dictionary.put("mon cabri", "chèvre ou bouc");
        dictionary.put("mon loto", "voiture");
        dictionary.put("mon tantine", "Expression qui désigne les filles de la Réunion");
        dictionary.put("la case", "Maison");

        authorisationGPS();


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void authorisationGPS() {

        locationManage = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //demander la permission de geolocalisation
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 7000);

        } else {
            //si j'ai la permission j'envoie une requet pour la localisation gps, en millisecond,mettre
            locationManage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
        }
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

        TextView text_trad = findViewById(R.id.trad_dico);



        if (requestCode== 202){
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Log.d("micro","-"+results.get(0));
            //recup le mot puis boucle sur les key du dico
            pattern = Pattern.compile(results.get(0));
            Iterator iter = dictionary.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry mEntry = (Map.Entry) iter.next();
                String key = (String) mEntry.getKey();
                matcher = pattern.matcher(key);

                //si match on traduis le mot sinon on envoie une popup erreur
                if (matcher.find()) {
                    String value = (String) (mEntry.getValue());
                    String def = key + ":  " + value;
                    text_trad.setText(def);

                    break;

                }




            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //recupère UI textView
        TextView addrTxt = this.findViewById(R.id.my_location);

        //convertie les données lat et log en adresse
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            String codePostal = addresses.get(0).getPostalCode();
            addrTxt.setText("Votre position actuel: "+addresses.get(0).getAddressLine(0));

            //Requet API des lieux historique par ville
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://api-monuments-re.herokuapp.com/monuments?code_postal="+codePostal;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {AllDataRep(response,location);}
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null
                            && error.networkResponse.statusCode == 404) {
                        Log.d("api","Pas de département trouvé. Veuillez vérifier le code saisi.");
                    } else {
                        Log.d("api","Erreur : " + error.getMessage());
                    }
                }
            });
            requestQueue.add(jsonArrayRequest);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void AllDataRep(JSONArray response, android.location.Location location) {
        double latitude = location.getLatitude();
        double longitude =  location.getLongitude();

        try {
            for (int i = 0; i < response.length(); i++) {

                JSONObject responseObj = response.getJSONObject(i);
                String code_postal = responseObj.getString("code_postal");
                String ville = responseObj.getString("ville");

                JSONArray lieux_historique = responseObj.getJSONArray("lieux_historique");
                int length = lieux_historique.length();

                for (int d = 0; i < length; d++) {

                    JSONObject data = lieux_historique.getJSONObject(d);

                    JSONArray coordinates = data.getJSONArray("coordonnees_finales");
                    String historique = data.getString("historique");
                    String nom = data.getString("appellation_courante");
                    double coordinates_latitude = (double) coordinates.get(0);
                    double coordinates_longitude = (double)coordinates.get(1);


                    distance(latitude, longitude, coordinates_latitude,
                            coordinates_longitude,nom,code_postal,ville,historique);

                }

            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }


    private void distance(double latitude, double longitude, double endLatitude, double endLongitude, String nom, String code_postal, String ville, String historique) throws IOException {
        Spinner list_monuments = this.findViewById(R.id.spinner_monuments);


        double theta = longitude - endLongitude;
        double dist = Math.sin(deg2rad(latitude))
                * Math.sin(deg2rad(endLatitude))
                + Math.cos(deg2rad(latitude))
                * Math.cos(deg2rad(endLatitude))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;


        if (dist<=2){
           // Log.d("gps","test distance "+String.format(Locale.FRENCH,"%,f",dist));
            //Log.d("api", "data " +nom + endLatitude + endLongitude);
            //ar.add(nom);
            Geocoder geocoder = new Geocoder(this);

            List<Address> addresses = geocoder.getFromLocation(endLatitude,longitude, 1);
            String adresse_lieux=addresses.get(0).getAddressLine(0);

            String data_spinner = nom+"("+adresse_lieux+")"+historique;

           ar.add(data_spinner);

            //Log.d("api", "data " +ar);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, ar);
            list_monuments.setAdapter(adapter);

            list_monuments.setOnItemSelectedListener(this);

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onItemSelectedHandler(parent, view, position, id);
    }

    private void onItemSelectedHandler(AdapterView<?> parent, View view, int position, long id) {
        TextView info_monuments = this.findViewById(R.id.info);

        Adapter adapter = parent.getAdapter();
        //Log.d("spinner","   "+adapter.getItem(position));

        info_monuments.setText(adapter.getItem(position).toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        
    }


    private double rad2deg(double dist) {
        return (dist * 180.0 / Math.PI);

    }

    private double deg2rad(double latitude) {
        return (latitude * Math.PI / 180.0);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 7000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

            }
        }
    }

}