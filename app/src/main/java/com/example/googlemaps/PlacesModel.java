package com.example.googlemaps;

import java.io.Serializable;

public class PlacesModel implements Serializable {
    String strAddress;
    String strLat;
    String strLng;

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getStrLat() { return strLat; }

    public void setStrLat(String strLat) {
        this.strLat = strLat;
    }

    public String getStrLng() {
        return strLng;
    }

    public void setStrLng(String strLng) {
        this.strLng = strLng;
    }


}
