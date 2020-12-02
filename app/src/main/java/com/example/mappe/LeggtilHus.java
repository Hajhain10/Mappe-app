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
import android.widget.Toast;

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

    //initaliserer data
    TextView tekst;
    EditText beskrivelse;
    TextView adresse;
    EditText etasjer;
    String ut;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legg_til_hus);
        //henter data
        double latitude = getIntent().getExtras().getDouble("lat",0);
        double longitude = getIntent().getExtras().getDouble("long", 0);
        tekst = (TextView) findViewById(R.id.koordianter);
        beskrivelse = (EditText) findViewById(R.id.beskrivelse);
        adresse = (TextView) findViewById(R.id.adresse);
        etasjer = (EditText) findViewById(R.id.etasjer);
        ut = getIntent().getExtras().getString("addressen");

        adresse.setText(ut);
        tekst.setText(latitude+","+longitude);
    }
    public String getLocationFromnumber(String navn) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            String[] p = navn.split(",");
            Toast.makeText(this,
                    navn,
                    Toast.LENGTH_SHORT).show();

            System.out.println("aaaaaaaa" + navn);
            address = coder.getFromLocation(Double.parseDouble(p[0]), Double.parseDouble(p[1]), 1);
            // address = coder.getFromLocationName(saddress,1);

            Address location = address.get(0);
            return location.getAddressLine(0)+"nå";
        } catch (Exception e) {
            return "null";
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
        //sjekker om input er gyldig
        if(sjekkInput(beskrivelse.getText().toString(),etasjer.getText().toString())) {
            String url = "http://student.cs.oslomet.no/~s331409/husin.php/?Beskrivelse=" + beskrivelse.getText() +
                    "&Gateadresse=" + adresse.getText() + "&Koordinater=" + tekst.getText() +
                    "&Antalletasjer=" + etasjer.getText() + "";
            String urlen = url.replaceAll(" ", "%20");
            leggTil task = new leggTil();
            task.execute(new String[]{urlen});
            finish();
        }
    }
    public boolean sjekkInput(String beskrivelse, String antallEtasjer){
        //sjekker om beskrivelse-feltet er tom, eller om det er større enn varchar(100)
        if(beskrivelse.length() > 100 || beskrivelse.length() <= 0 ){
            Toast toast = Toast.makeText(this, "beskrivelse", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("beskrivelse");
            return false;
        }
        //sjekker om antallEtasjer-feltet er tom, eller om det er større enn varchar(2)
        if(antallEtasjer.length() > 2 || antallEtasjer.length() <= 0 || antallEtasjer.equals("0")){
            Toast toast = Toast.makeText(this, "etasjenr", Toast.LENGTH_SHORT);
            toast.show();
            System.out.println("etasjenr");
            return false;
        }
        return true;
    }
}
