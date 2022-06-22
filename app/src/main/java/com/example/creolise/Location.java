package com.example.creolise;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity implements LocationListener {

    private LocationManager locationManage;
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

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


    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {

      //  Log.d("gps", location.getLatitude() + " - " + location.getLongitude());
        //convertie les données lat et log en adresse
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

           String codePostal = addresses.get(0).getPostalCode();
            System.out.println(addresses.get(0).getAddressLine(0));

            //recupère UI textView
            TextView latText = this.findViewById(R.id.latitude);
            TextView logText = this.findViewById(R.id.logitude);
            TextView addrTxt = this.findViewById(R.id.adresse);

            //Insert les coordonnée gps dans les ui
            latText.setText(location.getLatitude()+"");
            logText.setText(location.getLongitude()+"");
            addrTxt.setText(addresses.get(0).getAddressLine(0));

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            /*String url = "https://api-adresse.data.gouv.fr/search/?q=reunion&postcode="+codePostal;


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            fillDepartementResultat(response ,location);
                            //Log.d("api","reponse "+response);

                        }
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
            requestQueue.add(request);*/

            String url = "https://api-monuments-re.herokuapp.com/monuments?code_postal="+codePostal;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    AllDataRep(response,location);

                }
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

                    // we are getting each json object.
                    JSONObject responseObj = response.getJSONObject(i);
                    // similarly we are extracting all the strings from our json object.
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


                           // Log.d("api", "data " +nom + coordinates_longitude + coordinates_latitude);

                        }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    private void distance(double latitude, double longitude, double endLatitude, double endLongitude, String nom, String code_postal, String ville, String historique) {

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
            Log.d("gps","test distance "+String.format(Locale.FRENCH,"%,f",dist));

            Log.d("api", "data " +nom + endLatitude + endLongitude);

        }

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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

            }
        }
    }


}
