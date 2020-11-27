package com.example.mappe;

public class Hus {
    //initaliserer
    private String beskrivelse, gateadresse, koordinater, antallEtasjer;
    private int id;
    //open konstruktør dersom d trengs
    public Hus(){}

    public Hus(int id,String beskrivelse, String gateadresse, String koordinater, String antallEtasjer) {
        this.id = id;
        this.beskrivelse = beskrivelse;
        this.gateadresse = gateadresse;
        this.koordinater = koordinater;
        this.antallEtasjer = antallEtasjer;
    }

    //get og set metoder for alle
    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getGateadresse() {
        return gateadresse;
    }

    public void setGateadresse(String gateadresse) {
        this.gateadresse = gateadresse;
    }

    public String getKoordinater() {
        return koordinater;
    }

    public void setKoordinater(String kapasitet) {
        this.koordinater = kapasitet;
    }

    public String getAntallEtasjer() {
        return antallEtasjer;
    }

    public void setAntallEtasjer(String antallEtasjer) {
        this.antallEtasjer = antallEtasjer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
