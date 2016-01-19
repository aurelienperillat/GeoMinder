package com.geominder.esiea.geonav;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ModifierActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private EditText editTitre;
    private TimePickerDialog tpdDepart, tpdFin;
    private TextView timeDepart, timeFin, lieu;
    private TextView L,Ma,Me,J,V,S,D;
    private boolean dayState[] = {false,false,false,false,false,false,false};
    private ArrayList<Alarme> listAlarme = new ArrayList<Alarme>();
    private File fileAlarme;
    private int position;
    private Place place = null;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

        Intent intent = getIntent();
        position = intent.getIntExtra(GererActivity.POSITION,0);

        editTitre = (EditText)findViewById(R.id.edit_titre);
        lieu = (TextView)findViewById(R.id.tv_lieu);
        timeDepart = (TextView)findViewById(R.id.edit_depart);
        timeFin = (TextView)findViewById(R.id.edit_fin);
        L = (TextView)findViewById(R.id.tv_lundi);
        Ma = (TextView)findViewById(R.id.tv_mardi);
        Me = (TextView)findViewById(R.id.tv_mercredi);
        J = (TextView)findViewById(R.id.tv_jeudi);
        V = (TextView)findViewById(R.id.tv_vendredi);
        S = (TextView)findViewById(R.id.tv_samedi);
        D = (TextView)findViewById(R.id.tv_dimanche);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

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


        String filePath = this.getFilesDir().getPath().toString() + "/liste_alarme.txt";
        fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadListAlarmeFromFile(listAlarme, fileAlarme);

        Alarme alarme = listAlarme.get(position);

        tpdDepart = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute == 0)
                    time = hourOfDay+":00";
                else
                    time = hourOfDay+":"+minute;

                timeDepart.setText(time);
                timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
            }
        },12,0,true);

        tpdFin = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute == 0)
                    time = hourOfDay+":00";
                else
                    time = hourOfDay+":"+minute;

                timeFin.setText(time);
                timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));
            }
        },12,0,true);

        editTitre.setText(alarme.getTitre());
        lieu.setText(alarme.getLieu());
        timeDepart.setText(alarme.getTimeStart());
        timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
        timeFin.setText(alarme.getTimeEnd());
        timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));

        alarme.getDayState(dayState);
        for(int i = 0; i < dayState.length; i++){
            if(dayState[i] == true)
                Log.d("TAG", "daystate["+i+"] = true");
            else
                Log.d("TAG", "daystate["+i+"] = false");
        }

        if(dayState[0] == true) {
            L.setTextColor(getResources().getColor(R.color.BelizeHole));
            L.setText("L");
        }
        if(dayState[1] == true) {
            Ma.setTextColor(getResources().getColor(R.color.BelizeHole));
            Ma.setText("M");
        }
        if(dayState[2] == true) {
            Me.setTextColor(getResources().getColor(R.color.BelizeHole));
            Me.setText("M");
        }
        if(dayState[3] == true) {
            J.setTextColor(getResources().getColor(R.color.BelizeHole));
            J.setText("J");
        }
        if(dayState[4] == true) {
            V.setTextColor(getResources().getColor(R.color.BelizeHole));
            V.setText("V");
        }
        if(dayState[5] == true) {
            S.setTextColor(getResources().getColor(R.color.BelizeHole));
            S.setText("S");
        }
        if(dayState[6] == true) {
            D.setTextColor(getResources().getColor(R.color.BelizeHole));
            D.setText("D");
        }

        if(dayState[0] == false) {
            L.setTextColor(getResources().getColor(R.color.Silver));
            L.setText(Html.fromHtml("<i>L</i>"));
        }
        if(dayState[1] == false) {
            Ma.setTextColor(getResources().getColor(R.color.Silver));
            Ma.setText(Html.fromHtml("<i>M</i>"));
        }
        if(dayState[2] == false) {
            Me.setTextColor(getResources().getColor(R.color.Silver));
            Me.setText(Html.fromHtml("<i>M</i>"));
        }
        if(dayState[3] == false) {
            J.setTextColor(getResources().getColor(R.color.Silver));
            J.setText(Html.fromHtml("<i>J</i>"));
        }
        if(dayState[4] == false) {
            V.setTextColor(getResources().getColor(R.color.Silver));
            V.setText(Html.fromHtml("<i>V</i>"));
        }
        if(dayState[5] == false) {
            S.setTextColor(getResources().getColor(R.color.Silver));
            S.setText(Html.fromHtml("<i>S</i>"));
        }
        if(dayState[6] == false) {
            D.setTextColor(getResources().getColor(R.color.Silver));
            D.setText(Html.fromHtml("<i>D</i>"));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);
                lieu.setText(place.getName());
                lieu.setTextColor(getResources().getColor(R.color.BelizeHole));
                Log.d("Place Picker", "result OK !");
            }
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

    public void selectPlace(View v){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void enregistrerAction(View v){
        Alarme alarme = new Alarme(editTitre.getText().toString(),place.getName().toString(),
                timeDepart.getText().toString(),timeFin.getText().toString(),place.getAddress().toString(),
                place.getLatLng(),dayState,true);
        listAlarme.remove(position);
        listAlarme.add(position, alarme);

        saveListAlarmeToFile(listAlarme, fileAlarme);

        Toast.makeText(getApplicationContext(), "Alarme modifiée !", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
    }

    public void supprimerAction(View v){
        listAlarme.remove(position);
        saveListAlarmeToFile(listAlarme, fileAlarme);

        Toast.makeText(getApplicationContext(), "Alarme suprimée !", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
    }

    public void homeAction(View v){
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
    }

    public void showTimeDepartAction(View v){
        tpdDepart.show();
    }

    public void showTimeFinAction(View v){
        tpdFin.show();
    }

    public void lundiAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_lundi);

        if(dayState[0] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("L");
            dayState[0] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>L</i>"));
            dayState[0] = false;
        }
    }

    public void mardiAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_mardi);

        if(dayState[1] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("M");
            dayState[1] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>M</i>"));
            dayState[1] = false;
        }
    }

    public void mercrediAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_mercredi);

        if(dayState[2] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("M");
            dayState[2] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>M</i>"));
            dayState[2] = false;
        }
    }

    public void jeudiAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_jeudi);

        if(dayState[3] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("J");
            dayState[3] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>J</i>"));
            dayState[3] = false;
        }
    }

    public void vendrediAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_vendredi);

        if(dayState[4] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("V");
            dayState[4] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>V</i>"));
            dayState[4] = false;
        }
    }

    public void samediAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_samedi);

        if(dayState[5] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("S");
            dayState[5] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>S</i>"));
            dayState[5] = false;
        }
    }

    public void dimancheAction(View v){
        TextView textView = (TextView)findViewById(R.id.tv_dimanche);

        if(dayState[6] == false) {
            textView.setTextColor(getResources().getColor(R.color.BelizeHole));
            textView.setText("D");
            dayState[6] = true;
        }
        else {
            textView.setTextColor(getResources().getColor(R.color.Silver));
            textView.setText(Html.fromHtml("<i>D</i>"));
            dayState[6] = false;
        }
    }


    private void saveListAlarmeToFile(ArrayList<Alarme> listAlarme, File file) {
        try{
            ObjectOutputStream dataOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            for (Alarme alarme : listAlarme)
                dataOut.writeObject(alarme);

            dataOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListAlarmeFromFile(ArrayList<Alarme> listAlarme, File file){
        try{
            ObjectInputStream dataIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

            Alarme alarme;
            try {

                while ((alarme = (Alarme)dataIn.readObject()) != null)
                    listAlarme.add(alarme);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            dataIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
