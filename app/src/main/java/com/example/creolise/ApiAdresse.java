package com.example.creolise;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

        String url = "https://api-adresse.data.gouv.fr/search/?q=reunion&postcode=97420";

     /*   StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // traitement en cas de succès
                        Log.d("api","data "+response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // traitement en cas d'échec
                Log.d("api","data "+ error);

            }
        });*/

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        fillDepartementResultat(response);
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
                requestQueue.add(request);








    }

    private void fillDepartementResultat(JSONObject jsonObject) {
        try {
            JSONArray features = jsonObject.getJSONArray("features");
            int length = features.length();

            for (int i = 0; i < length; i++) {

                JSONObject data = features.getJSONObject(i);
                JSONObject geometry = data.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                JSONObject properties = data.getJSONObject("properties");
                String adresse = properties.getString("label");
                String postcode = properties.getString("postcode");
                String citycode = properties.getString("citycode");

                Log.d("api","data "+coordinates.get(0)+" - "+coordinates.get(1)+" - "+adresse
                +" -" +postcode+" -" +citycode
                );


            }


        } catch (JSONException e) {
            Log.d("api","Erreur : " + e.getMessage());
        }
    }
}