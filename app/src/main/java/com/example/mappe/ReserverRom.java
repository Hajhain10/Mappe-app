package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserverRom extends AppCompatActivity {
    TextView husid, romid, dato, tid;
    String hus_id="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_reservasjon);
        husid = (TextView) findViewById(R.id.hus);
        romid = (TextView) findViewById(R.id.romnummer);
        dato = (TextView) findViewById(R.id.dato);
        tid = (TextView) findViewById(R.id.tid);
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
    public void lagNyttReservasjon(View view) {
        System.out.println(hus_id);
        String[] biter = tid.getText().toString().split("\n");
        for(int i = 0; i<biter.length;i++) {
            String[] biter2 = biter[i].split("til");
            String start = biter2[0];
            System.out.println(start);
            String slutt = biter2[1];
            System.out.println(start);
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
