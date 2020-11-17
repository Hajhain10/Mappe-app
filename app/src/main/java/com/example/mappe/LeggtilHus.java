package com.example.mappe;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LeggtilHus extends AppCompatActivity {

    TextView tekst;
    EditText beskrivelse;
    EditText adresse;
    EditText etasjer;
    String ut;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_hus);
        double latitude = getIntent().getExtras().getDouble("lat",0);
        double longitude = getIntent().getExtras().getDouble("long", 0);
        tekst = (TextView) findViewById(R.id.koordianter);
        beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        adresse = (EditText) findViewById(R.id.adresse);
        etasjer = (EditText) findViewById(R.id.etasjer);
        ut = getLocationFromnumber(latitude+","+longitude);
        adresse.setText(ut);
        tekst.setText(latitude+","+longitude);


    }
    public String getLocationFromnumber(String navn) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        try {
            String[] p = navn.split(",");

            System.out.println("aaaaaaaa" + navn);
            address = coder.getFromLocation(Double.parseDouble(p[0]), Double.parseDouble(p[1]), 1);
            // address = coder.getFromLocationName(saddress,1);

            Address location = address.get(0);
            return location.getAddressLine(0).toString();
        } catch (Exception e) {
            return null;
        }
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

    public void lagNyttHus(View view) {
        String url = "http://student.cs.oslomet.no/~s331409/husin.php/?Beskrivelse="+beskrivelse.getText()+
                "&Gateadresse="+adresse.getText()+"&Koordinater="+tekst.getText()+
                "&Antalletasjer="+etasjer.getText()+"";
        String urlen= url.replaceAll(" ","%20");
        leggTil task = new leggTil();
        Log.i("urlforja",urlen);
        task.execute(new String[]{urlen});
        finish();
    }
}
