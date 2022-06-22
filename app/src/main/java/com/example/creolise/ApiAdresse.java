package com.example.creolise;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiAdresse extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://api-monuments-re.herokuapp.com/monuments?code_postal=97400";


      /*  JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        fillDepartementResultat(response);
                        Log.d("api","reponse "+response);

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
                requestQueue.add(request);

*/


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                AllDataRep(response);

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

    }

    private void AllDataRep(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {

            try {
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

                   // String adresse = data.getString("adresse");

                    String nom = data.getString("appellation_courante");

                    double coordinates_latitude = (double) coordinates.get(0);
                    double coordinates_longitude = (double) coordinates.get(1);

                    Log.d("api","data "+code_postal+ville+historique+nom+coordinates_longitude+coordinates_latitude);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

   /* private void fillDepartementResultat(JSONObject jsonObject) {

       // Log.d("api","-"+jsonObject);


    }*/



}