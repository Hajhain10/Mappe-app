package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Husinfo extends AppCompatActivity {
    TextView adresse, koordinater, etasjer, beskrivelse;
    String husid="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.husinfo);
        adresse = (TextView) findViewById(R.id.husadresse);
        koordinater = (TextView) findViewById(R.id.koordianter);
        etasjer = (TextView) findViewById(R.id.etasjer);
        beskrivelse = (TextView) findViewById(R.id.beskrivelse);
        husid = getIntent().getStringExtra("idhus");

        HuskJSON task = new HuskJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/husout.php"});
    }
    protected class HuskJSON extends AsyncTask<String, Void, Hus> {
        JSONObject jsonObject;
        Hus etHus = new Hus();
        @Override
        protected Hus doInBackground(String... urls) {
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
                    try{
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject= mat.getJSONObject(i);
                            int id = jsonobject.getInt("id");
                            String beskrivelse = jsonobject.getString("beskrivelse");
                            String gateadresse= jsonobject.getString("gateadresse");
                            String koordinater= jsonobject.getString("koordinater");
                            String antalletasjer= jsonobject.getString("antalletasjer");
                            retur = retur +id + beskrivelse +" "+gateadresse +" "+koordinater +
                                    " "+antalletasjer +"\n";
                            if(String.valueOf(id).equals(husid)){
                                etHus = new Hus(id,beskrivelse, gateadresse, koordinater, antalletasjer);
                                return etHus;
                            }


                            System.out.println("ccccccc "+retur );
                        }
                        return etHus;
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                } catch(Exception e) {
                    return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Hus ss) {
           adresse.setText(ss.getGateadresse());
           koordinater.setText(ss.getKoordinater());
           etasjer.setText(ss.getAntallEtasjer());
           beskrivelse.setText(ss.getBeskrivelse());
        }
    }
}
