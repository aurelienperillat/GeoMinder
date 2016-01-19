package com.geominder.esiea.geonav;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

public class GererActivity extends AppCompatActivity {
    private ArrayList<Alarme> listAlarme = new ArrayList<Alarme>();
    private File fileAlarme;
    protected RecyclerView recyclerView;

    public final static String POSITION = "com.geominder.esiea.geonav.position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerer);

        recyclerView = (RecyclerView)findViewById(R.id.listeAlarme);

        String filePath = this.getFilesDir().getPath().toString() + "/liste_alarme.txt";
        fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadListAlarmeFromFile(listAlarme, fileAlarme);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new AlarmAdapter(listAlarme));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void homeAction(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void ajouterAction(View v){
        Intent intent = new Intent(this, AjouterActivity.class);
        startActivity(intent);
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

    class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {
        private ArrayList<Alarme> listAlarme = new ArrayList<Alarme>();

        public AlarmAdapter(ArrayList<Alarme> listAlarme){
            this.listAlarme = listAlarme;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_alarme_element, parent, false);
            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            holder.titre.setText(listAlarme.get(position).getTitre());

            if (listAlarme.get(position).getIsActivated() == true) {
                holder.state.setChecked(true);
                holder.titre.setTextColor(getResources().getColor(R.color.BelizeHole));
            }
            else {
                holder.state.setChecked(false);
                holder.titre.setTextColor(getResources().getColor(R.color.Silver));
            }
        }

        @Override
        public int getItemCount() {
            return listAlarme.size();
        }

        class AlarmHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
            public TextView titre;
            public CheckBox state;

            public AlarmHolder(View itemView){
                super(itemView);
                this.titre = (TextView)itemView.findViewById(R.id.element_titre);
                this.state = (CheckBox)itemView.findViewById(R.id.element_state);

                itemView.setClickable(true);
                itemView.setOnLongClickListener(this);

                this.state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition();

                        if(state.isChecked()) {
                            titre.setTextColor(getResources().getColor(R.color.BelizeHole));
                            listAlarme.get(position).setIsActivated(true);
                        }
                        else {
                            titre.setTextColor(getResources().getColor(R.color.Silver));
                            listAlarme.get(position).setIsActivated(false);
                        }

                        saveListAlarmeToFile(listAlarme,fileAlarme);
                    }
                });
            }

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getBaseContext(), ModifierActivity.class);
                int position = getPosition();
                intent.putExtra(POSITION,position);
                startActivity(intent);
                return false;
            }
        }
    }
}
