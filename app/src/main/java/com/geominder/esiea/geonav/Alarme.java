package com.geominder.esiea.geonav;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Alarme implements Serializable{
    private String titre, lieu, timeStart, timeEnd, adresse;
    private double latitude, longitude;
    private boolean[] daystate = {false,false,false,false,false,false,false};
    private boolean isActivated;

    public Alarme(String titre, String lieu, String timeStart, String timeEnd, String adresse, LatLng latLng, boolean[] daySate, boolean isActivated){
        this.titre = titre;
        this.lieu = lieu;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.adresse = adresse;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        for(int i = 0; i < daySate.length; i++ )
            this.daystate[i] = daySate[i];
        this.isActivated = isActivated;
    }

    public String getTitre(){ return titre; }
    public String getLieu(){ return lieu; }
    public String getAdresse () {return adresse;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    public String getTimeStart(){ return timeStart; }
    public String getTimeEnd(){ return timeEnd; }
    public void getDayState(boolean[] tab){
        for(int i = 0; i < tab.length; i++ )
            tab[i] = daystate[i];
    }
    public boolean getIsActivated(){ return isActivated; }

    public void setIsActivated(boolean val){
        this.isActivated = val;
    }

    public String toString(){
        return "Titre: "+this.titre+"\n" +
                "Lieu: "+this.lieu+"\n" +
                "timeStard: "+this.timeStart+"\n" +
                "timeEnd: "+this.timeEnd+"\n" +
                "dayState: "+createStringFromBooleanArray(this.daystate)+"\n" +
                "isActivated: "+this.isActivated+"\n" ;
    }

    private String createStringFromBooleanArray(boolean[] tab){
        String str = null;
        for (int i = 0; i < tab.length; i++){
            if(tab[i])
                str = str + "true ";
            else
                str = str + "false ";
        }
        return str;
    }
}
