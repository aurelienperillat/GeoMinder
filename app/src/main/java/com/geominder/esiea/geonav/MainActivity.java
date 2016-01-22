package com.geominder.esiea.geonav;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<Alarme> listAlarme = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filePath = this.getFilesDir().getPath() + "/liste_alarme.txt";
        File fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadListAlarmeFromFile(listAlarme, fileAlarme);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            LatLng myLatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        for(Alarme alarme : listAlarme){
            if(alarme.getIsActivated()) {
                LatLng latLng = new LatLng(alarme.getLatitude(), alarme.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(alarme.getLieu())
                        .snippet(alarme.getTitre()));
            }
        }
    }

    public void ajouterAction(View v){
        Intent intent = new Intent(this, AjouterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void gererAction(View v){
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void loadListAlarmeFromFile(ArrayList<Alarme> listAlarme, File file) {
        try {
            ObjectInputStream dataIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

            Alarme alarme;
            try {

                while ((alarme = (Alarme) dataIn.readObject()) != null)
                    listAlarme.add(alarme);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            dataIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.d("Google Place", "Connexion établie");
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d("Google Place", "Connexion finis");
    }
}

