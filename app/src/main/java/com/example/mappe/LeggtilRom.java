package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeggtilRom extends AppCompatActivity {
    EditText romnummer, etasjenr, beskrivelse, kapasitet;
    TextView husadresse;
    String husid="";
    int antalletasjer=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_rom);
        romnummer = (EditText) findViewById(R.id.romnummer);
        etasjenr = (EditText) findViewById(R.id.etasje_nr);
        beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        kapasitet = (EditText) findViewById(R.id.kapasitet);
        husadresse = (TextView) findViewById(R.id.hus_id);

        husid = getIntent().getStringExtra("idhus");
        husadresse.setText(getIntent().getStringExtra("husnavn"));
        antalletasjer = Integer.parseInt(getIntent().getStringExtra("antalletasjer"));

    }
    private class leggTil extends AsyncTask<String, Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls){
                try{
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn= (HttpURLConnection)urlen.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept", "application/json");
                    if(conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed: HTTP errorcode: "+ conn.getResponseCode());
                    }
                    BufferedReader br= new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    while((s = br.readLine()) != null) { output = output + s; }
                    conn.disconnect();
                    return retur;
                } catch(Exception e) {
                    return"Noe gikk feil";
                }
            }
            return retur;
        }
        @Override
        protected void onPostExecute(String ss) { }
    }

    public void lagNyttRom(View view) {
        //sjekker om input er gyldig
        if(sjekkInput(romnummer.getText().toString(), beskrivelse.getText().toString(),
                etasjenr.getText().toString(), kapasitet.getText().toString())){
            String url = "http://student.cs.oslomet.no/~s331409/romin.php/?Romnummer="+
                    romnummer.getText()+"&Hus_id="+husid+"&Beskrivelse="+
                    beskrivelse.getText()+"&Etasjenr="+etasjenr.getText()+
                    "&Kapasitet="+kapasitet.getText()+"";
            //erstatter alle tomrom med %20
            String urlen= url.replaceAll(" ","%20");
            LeggtilRom.leggTil task = new LeggtilRom.leggTil();
            task.execute(new String[]{urlen});
            Toast.makeText(this,"Rom er lagt til",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //metode for å sjekke om input er gyldig
    public boolean sjekkInput(String romnummer, String beskrivelse, String etasjenr,
                              String kapasitet){
        //sjekker om romnummer-feltet er tom, eller om det er større enn int(3)
        if(romnummer.length() > 3 || romnummer.length() <= 0 || romnummer.equals("0")){
            Toast toast = Toast.makeText(this, "romnummer", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        //sjekker om etasjenr-feltet er tom, eller om det er større enn varchar(2)
        if(Integer.parseInt(etasjenr)>antalletasjer){
            Toast toast = Toast.makeText(this, "Maks antall etasjer er "+antalletasjer, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        //sjekker om kapasitet-feltet er tom, eller om det er større enn varchar(4)
        if(kapasitet.length() > 4 || kapasitet.length() <= 0 || kapasitet.equals("0")){
            Toast toast = Toast.makeText(this, "kapasitet", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        //sjekker om beskrivelse-feltet er tom, eller om det er større enn varchar(100)
        if(beskrivelse.length() > 100 || beskrivelse.length() <= 0 ){
            Toast toast = Toast.makeText(this, "beskrivelse", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //lagrer alt som trengs ved rotering
        outState.putString("romnummer",romnummer.getText().toString());
        outState.putString("etasjenr",etasjenr.getText().toString());
        outState.putString("beskrivelse",beskrivelse.getText().toString());
        outState.putString("kapasitet",kapasitet.getText().toString());
    }
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        //henter alle verdiene fra metoden over
        romnummer.setText(savedInstanceState.getString("romnummer"));
        etasjenr.setText(savedInstanceState.getString("etasjenr"));
        beskrivelse.setText(savedInstanceState.getString("beskrivelse"));
        kapasitet.setText(savedInstanceState.getString("kapasitet"));
    }
}
