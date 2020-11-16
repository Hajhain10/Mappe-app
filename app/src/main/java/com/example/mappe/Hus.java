package com.example.mappe;

public class Hus {
    private String beskrivelse, gateadresse, koordinater, antallEtasjer;

    public Hus(){}

    public Hus(String beskrivelse, String gateadresse, String koordinater, String antallEtasjer) {
        this.beskrivelse = beskrivelse;
        this.gateadresse = gateadresse;
        this.koordinater = koordinater;
        this.antallEtasjer = antallEtasjer;
    }

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
}
