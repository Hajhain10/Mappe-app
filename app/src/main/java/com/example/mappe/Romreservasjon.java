package com.example.mappe;

public class Romreservasjon {
    private int romnummer, husID;
    private String dato, starttid, slutttid;

    public Romreservasjon(){}

    @Override
    public String toString() {
        return "Romreservasjon{" +
                "romnummer=" + romnummer +
                ", husID=" + husID +
                ", dato='" + dato + '\'' +
                ", starttid='" + starttid + '\'' +
                ", slutttid='" + slutttid + '\'' +
                '}';
    }

    public Romreservasjon(int romnummer, int husID, String dato, String starttid, String slutttid) {
        this.romnummer = romnummer;
        this.husID = husID;
        this.dato = dato;
        this.starttid = starttid;
        this.slutttid = slutttid;
    }

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

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getStarttid() {
        return starttid;
    }

    public void setStarttid(String starttid) {
        this.starttid = starttid;
    }

    public String getSlutttid() {
        return slutttid;
    }

    public void setSlutttid(String slutttid) {
        this.slutttid = slutttid;
    }
}
