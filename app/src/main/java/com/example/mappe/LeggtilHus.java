package com.example.mappe;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LeggtilHus extends AppCompatActivity {

    TextView tekst;
    EditText beskrivelse;
    EditText adresse;
    EditText etasjer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_hus);
        double latitude = getIntent().getExtras().getDouble("lat",0);
        double longitude = getIntent().getExtras().getDouble("long", 0);
        tekst = (TextView) findViewById(R.id.koordianter);
        beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        adresse = (EditText) findViewById(R.id.adresse);
        etasjer = (EditText) findViewById(R.id.etasjer);
        String ut = getLocationFromnumber(latitude+","+longitude);
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

    public void lagNyttHus(View view) {

    }
}
