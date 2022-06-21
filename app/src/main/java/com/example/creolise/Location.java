package com.example.creolise;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import java.io.IOException;
import java.util.List;

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

        Log.d("gps", location.getLatitude() + " - " + location.getLongitude());
        //convertie les données lat et log en adresse
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

            Log.d("gps","data "+addresses.get(0).getAddressLine(0));
            System.out.println(addresses.get(0).getAddressLine(0));

            //recupère UI textView
            TextView latText = this.findViewById(R.id.latitude);
            TextView logText = this.findViewById(R.id.logitude);
            TextView addrTxt = this.findViewById(R.id.adresse);

            //Insert les coordonnée gps dans les ui
            latText.setText(location.getLatitude()+"");
            logText.setText(location.getLongitude()+"");

            addrTxt.setText(addresses.get(0).getAddressLine(0));



        }catch (IOException e){
            e.printStackTrace();
        }

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
