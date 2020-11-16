package com.example.mappe;

public class Rom {
    private int romnummer, husID;
    private String beskrivelse, kapasitet, etasjenr;

    public Rom(int romnummer, int husID, String beskrivelse, String kapasitet, String etasjenr) {
        this.romnummer = romnummer;
        this.husID = husID;
        this.beskrivelse = beskrivelse;
        this.kapasitet = kapasitet;
        this.etasjenr = etasjenr;
    }

    public Rom(){}
    public int getRomnummer() {
        return romnummer;
    }

    public void setRomnummer(int romnummer) {
        this.romnummer = romnummer;
    }

    public int getHusID() {
        return husID;
    }

    public void setHusID(int husID) {
        this.husID = husID;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getKapasitet() {
        return kapasitet;
    }

    public void setKapasitet(String kapasitet) {
        this.kapasitet = kapasitet;
    }

    public String getEtasjenr() {
        return etasjenr;
    }

    public void setEtasjenr(String etasjenr) {
        this.etasjenr = etasjenr;
    }
}
