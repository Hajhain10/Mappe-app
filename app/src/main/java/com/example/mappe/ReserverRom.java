package com.example.mappe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
}
