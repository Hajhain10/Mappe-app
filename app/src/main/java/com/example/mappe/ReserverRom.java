package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserverRom extends AppCompatActivity {
    //initaliserer data
    TextView husid, romid, dato, tid;
    String hus_id="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_reservasjon);
        husid = (TextView) findViewById(R.id.hus);
        romid = (TextView) findViewById(R.id.romnummer);
        dato = (TextView) findViewById(R.id.dato);
        tid = (TextView) findViewById(R.id.tid);
        //henter data
        hus_id = getIntent().getStringExtra("idhus");
        husid.setText(getIntent().getStringExtra("husnavn"));
        romid.setText(getIntent().getStringExtra("idrom"));
        dato.setText(getIntent().getStringExtra("dato"));
        tid.setText(skrivOmTid(getIntent().getStringExtra("tider")));
    }
    private String skrivOmTid(String tid){
        //Dette gir antall tider som skal legges til
        String ut="";
        String[] tider = tid.split(",");
        for (int i=0;i<tider.length;i++){
            ut+=tider[i].replaceAll(" "," til ")+"\n";
        }
        return ut;
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
    public void lagNyttReservasjon(View view) {
        //knapp metode for å legge inn
        Toast.makeText(this,"Lagret",
                Toast.LENGTH_SHORT).show();
        //Deler opp tid-feltet på hver linjeskift
        String[] biter = tid.getText().toString().split("\n");
        for(int i = 0; i<biter.length;i++) {
            //Deler opp linja på "til" for å få start- og slutttid
            String[] biter2 = biter[i].split("til");
            //forste streng blir starttid
            String start = biter2[0];
            //andre streng blir slutttid
            String slutt = biter2[1];
            //fjerner all tomrom fra slutt- og starttid strengene
            String url = "http://student.cs.oslomet.no/~s331409/romreservasjonin.php/?Romnummer=" + romid.getText() +
                    "&Hus_id=" + hus_id+ "&Dato=" + dato.getText() + "&Starttid="
                    + start.replaceAll(" ", "") +
                    "&Slutttid=" + slutt.replaceAll(" ", "") + "";
            String urlen = url.replaceAll(" ", "%20");
            ReserverRom.leggTil task = new ReserverRom.leggTil();
            Log.i("urlforja", urlen);
            task.execute(new String[]{urlen});
        }
        finish();
    }
}
