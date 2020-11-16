package com.example.mappe;

public class Hus {
    private String beskrivelse, gateadresse, kapasitet, antallEtasjer;

    public Hus(String beskrivelse, String gateadresse, String kapasitet, String antallEtasjer) {
        this.beskrivelse = beskrivelse;
        this.gateadresse = gateadresse;
        this.kapasitet = kapasitet;
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

    public String getKapasitet() {
        return kapasitet;
    }

    public void setKapasitet(String kapasitet) {
        this.kapasitet = kapasitet;
    }

    public String getAntallEtasjer() {
        return antallEtasjer;
    }

    public void setAntallEtasjer(String antallEtasjer) {
        this.antallEtasjer = antallEtasjer;
    }
}
