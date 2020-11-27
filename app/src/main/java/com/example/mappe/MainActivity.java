package com.example.mappe;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener {
    //initaliserer verdier som brukes
    protected GoogleMap nMap;
    protected Marker marker;
    String id,husnavn="";
    String antalletasjer="";
    Button romknapp;
    Button nyttsted;
    Button avbrytknapp;
    Button husinfo;
    double latitude, longitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //kobler knapper opp mot xml layouten
        romknapp = (Button) findViewById(R.id.leie);
        nyttsted = (Button) findViewById(R.id.nyttsted);
        avbrytknapp = (Button) findViewById(R.id.avbryt);
        husinfo = (Button) findViewById(R.id.husinfo);
        husinfo.setVisibility(View.GONE);
        avbrytknapp.setVisibility(View.GONE);
        romknapp.setVisibility(View.GONE);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //asynctask kjøres
        HuskJSON task = new HuskJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/husout.php"});
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        LatLng startsted = new LatLng(59.9238031,10.7292638);
       // LatLng l = new LatLng(59.9241992,10.9560766);
       // nMap.addMarker(new MarkerOptions().position(l).title("Gammwlt hus").snippet("sniper deg 2 ganger"));
       // nMap.addMarker(new MarkerOptions().position(startsted).title("OSLO").snippet("snipped"));
        float zoom = 14.0f;
        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startsted,zoom));

        nMap.setOnMarkerClickListener(this);
        //nMap.setOnMapClickListener(this);
    }
    public boolean onMarkerClick(final Marker marker) {
        //dersom markør blir trykket så kommer disse
        romknapp.setVisibility(View.VISIBLE);
        husinfo.setVisibility(View.VISIBLE);
        //henter verdier fra snippet som ble lagra av onExecute i asynctask
        String[] liste = marker.getSnippet().split(" ");
        id = liste[0];
        antalletasjer = liste[1];
        husnavn = marker.getTitle();
        Toast.makeText(this,"Klikk <rom> for å lage rom i "+marker.getTitle(),
                Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    public void onMapClick(LatLng latLng) {
        if (marker!=null){
            marker.remove();
        }
        //dersom man skal lage et nytt sted
        MarkerOptions m = new MarkerOptions();
        m.position(latLng);
        m.title("den nye");
        marker = nMap.addMarker(m);
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        String send = marker.getPosition().latitude +","+ marker.getPosition().longitude;
    }
    public String getLocationFromAddress(String saddress){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(saddress,1);
            if (saddress==null){
                return null;
            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double ing = location.getLongitude();
            return lat +","+ ing;
        }catch (Exception e){
            return null;
        }
    }

    public String getLocationFromnumber(String navn) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        try {
            String[] p = navn.split(",");

            System.out.println("aaaaaaaa"+navn);
            address = coder.getFromLocation(Double.parseDouble(p[0]),Double.parseDouble(p[1]),1);
            // address = coder.getFromLocationName(saddress,1);

            Address location = address.get(0);
            System.out.println("bbbb"+location.getThoroughfare()+location.getAdminArea()+location.getFeatureName()
                    +location.getSubLocality());
            return location.getAddressLine(0).toString();
        }catch (Exception e){
            return null;
        }
    }

    public void nyttSted(View view) {
        //knappen legg til nytt sted
        //dersom alt er riktig så sendes du videre
        if(longitude ==0){
            Toast.makeText(this,
                    "velg på kartet der du vil legge til et nytt hus",
                    Toast.LENGTH_SHORT).show();
        }
        else if(nyttsted.getText().toString().equals("trykk her når ferdig")){
            Intent i = new Intent(this,LeggtilHus.class);
            i.putExtra("lat",latitude);
            i.putExtra("long",longitude);
            startActivity(i);
            recreate();
        }
        avbrytknapp.setVisibility(View.VISIBLE);
        romknapp.setVisibility(View.GONE);
        nyttsted.setText("trykk her når ferdig");
        Toast.makeText(this,
                "velg på kartet der du vil legge til et nytt hus",
                Toast.LENGTH_SHORT).show();
        nMap.setOnMapClickListener(this);
        nMap.setOnMarkerClickListener(null);
    }

    public void avbryt(View view) {
        //dersom man avbryter så starter vi siden på nytt
       recreate();
    }

    public void tilRom(View view) {
        //bytter til rom siden
        Intent i = new Intent(this,Rom_side.class);
       i.putExtra("hus_id",id);
       i.putExtra("husnavn",husnavn);
       i.putExtra("antalletasjer",antalletasjer);
        startActivity(i);
        //sender også med id nummer for huset.
    }

    public void visHusinfo(View view) {
        //viser hus info
        Intent i = new Intent(this, Husinfo.class);
        i.putExtra("idhus",id);
        startActivity(i);
    }

    protected class HuskJSON extends AsyncTask<String, Void,ArrayList<Hus>> {
        JSONObject jsonObject;
        ArrayList<Hus> ny = new ArrayList<>();
        Hus etHus = new Hus();
        @Override
        protected ArrayList<Hus> doInBackground(String... urls) {
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
                        JSONArray husobjekter = new JSONArray(output);
                        for (int i = 0; i < husobjekter.length(); i++) {
                            JSONObject jsonobject= husobjekter.getJSONObject(i);
                            int id = jsonobject.getInt("id");
                            String beskrivelse = jsonobject.getString("beskrivelse");
                            String gateadresse= jsonobject.getString("gateadresse");
                            String koordinater= jsonobject.getString("koordinater");
                            String antalletasjer= jsonobject.getString("antalletasjer");
                            retur = retur +id + beskrivelse +" "+gateadresse +" "+koordinater +
                                    " "+antalletasjer +"\n";

                            etHus = new Hus(id,beskrivelse, gateadresse, koordinater, antalletasjer);
                            ny.add(etHus);
                        }
                        return ny;
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    return ny;
                } catch(Exception e) {
                    return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Hus> ss) {
            //etter å ha fått inn alle objektene
           for(int i= 0; i<ss.size();i++){
               String[] latlng = ss.get(i).getKoordinater().split(",");
               LatLng pos = new LatLng(Double.parseDouble(latlng[0]),Double.parseDouble(latlng[1]));

               nMap.addMarker(new MarkerOptions().position(pos).title(ss.get(i).getGateadresse())
                       .snippet(String.valueOf(ss.get(i).getId())+" "+ss.get(i).getAntallEtasjer()));
           }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HuskJSON task = new HuskJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/husout.php"});
    }
}