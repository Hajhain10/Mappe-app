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
    TextView husid;
    String antalletasjer="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_rom);
        romnummer = (EditText) findViewById(R.id.romnummer);
        etasjenr = (EditText) findViewById(R.id.etasje_nr);
        beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        kapasitet = (EditText) findViewById(R.id.kapasitet);
        husid = (TextView) findViewById(R.id.hus_id);

        husid.setText(getIntent().getStringExtra("idhus"));
        antalletasjer = getIntent().getStringExtra("antalletasjer");
        Toast toast = Toast.makeText(this, antalletasjer, Toast.LENGTH_SHORT);
        toast.show();
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
                    System.out.println("Output from Server .... \n");
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
        if(sjekkInput(romnummer.getText().toString(), beskrivelse.getText().toString(),
                etasjenr.getText().toString(), kapasitet.getText().toString())){
            String url = "http://student.cs.oslomet.no/~s331409/romin.php/?Romnummer="+
                    romnummer.getText()+"&Hus_id="+husid.getText()+"&Beskrivelse="+
                    beskrivelse.getText()+"&Etasjenr="+etasjenr.getText()+
                    "&Kapasitet="+kapasitet.getText()+"";
            String urlen= url.replaceAll(" ","%20");
            LeggtilRom.leggTil task = new LeggtilRom.leggTil();
            Log.i("urlforja",urlen);
            task.execute(new String[]{urlen});
            finish();
        }
    }

    public boolean sjekkInput(String romnummer, String beskrivelse, String etasjenr,
                              String kapasitet){
        if(romnummer.length() > 3 || romnummer.length() <= 0 || romnummer.equals("0")){
            Toast toast = Toast.makeText(this, "romnummer", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if(beskrivelse.length() > 100 || beskrivelse.length() <= 0 ){
            Toast toast = Toast.makeText(this, "beskrivelse", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("beskrivelse");
            return false;
        }
        if(etasjenr.length() > 2 || etasjenr.length() <= 0 || etasjenr.equals("0")){
            Toast toast = Toast.makeText(this, "etasjenr", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("etasjenr");
            return false;
        }
        if(kapasitet.length() > 4 || kapasitet.length() <= 0 || kapasitet.equals("0")){
            Toast toast = Toast.makeText(this, "kapasitet", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("kapasitet");
            return false;
        }
        return true;
    }
}
