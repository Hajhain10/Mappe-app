package com.example.mappe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Rom_side extends AppCompatActivity {
    //initaliserer data
    ListView lv;
    List<String> liste = new ArrayList<>();
    String id, romnavn = "";
    String idhus, husnavn, antalletasjer="";
    Button rom;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rom_side);
        //henter data
        idhus = getIntent().getStringExtra("hus_id");
        husnavn = getIntent().getStringExtra("husnavn");
        antalletasjer = getIntent().getStringExtra("antalletasjer");
        lv = (ListView)findViewById(R.id.personer);
        rom = (Button) findViewById(R.id.reserverRom);
        rom.setVisibility(View.GONE);
        RomJSON task = new RomJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romout.php"});

    }
    protected void setListe(ArrayList<String> listen){
        //oppdaterer listen
        liste = listen;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liste);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String trakk = adapter.getItem(i);
                    String[] lb = trakk.split(" ");
                    id = lb[1].toString();
                    rom.setVisibility(View.VISIBLE);
                    System.out.println("mmmmm " + id + "," + idhus);
                }
            });
        }

    public void LagReservsjon(View view) {
        //sjekker om rom er valgt
        if (id == null) {
            Toast toast = Toast.makeText(this, "Vennligst trykk på et rom", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            //bytter side
            Intent i = new Intent(this, Reservasjon.class);
            i.putExtra("idhus", idhus);
            i.putExtra("idrom", id);
            i.putExtra("husnavn", husnavn);
            i.putExtra("romnavn", romnavn);
            startActivity(i);
            // Toast toast = Toast.makeText(this, "Vennligst velg et rom", Toast.LENGTH_SHORT);
            //toast.show();
        }
    }

    public void leggInnNyttrom(View view) {
        //bytter side
        Intent i = new Intent(this, LeggtilRom.class);
        i.putExtra("idhus", idhus);
        i.putExtra("idrom", id);
        i.putExtra("husnavn", husnavn);
        i.putExtra("romnavn", romnavn);
        i.putExtra("antalletasjer",antalletasjer);
        startActivity(i);
    }

    public void RomInfo(View view) {
        if (id == null) {
            Toast toast = Toast.makeText(this, "Vennligst trykk på et rom", Toast.LENGTH_SHORT);
            toast.show();
        }else {
            //btter side dersom rom er valgt
            Intent i = new Intent(this, Rominfo.class);
            i.putExtra("romid", id);
            i.putExtra("husid", idhus);
            i.putExtra("husnavn", husnavn);
            startActivity(i);
        }
    }
    public void onResume(){
        super.onResume();
        //oppdaterer listen hver gang den er på onResume
        RomJSON task = new RomJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romout.php"});
    }

    protected class RomJSON extends AsyncTask<String, Void,ArrayList<String>> {
        JSONObject jsonObject;
        ArrayList<String> ny = new ArrayList<>();
        Rom rom = new Rom();
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
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
                        JSONArray rom = new JSONArray(output);
                        for (int i = 0; i < rom.length(); i++) {
                            JSONObject jsonobject= rom.getJSONObject(i);

                            int hus_id= jsonobject.getInt("hus_id");
                            //dersom den er i huset vi trakk på så henter den rommene som er laget
                            if(idhus.equals(String.valueOf(hus_id))) {
                                int romnummer = jsonobject.getInt("romnummer");
                                String beskrivelse= jsonobject.getString("beskrivelse");
                                String kapasitet= jsonobject.getString("kapasitet");
                                String etasjenr= jsonobject.getString("etasjenr");

                                retur =  "romnummer "+romnummer +" etasjenummer : "+etasjenr+
                                        " \nkapasitet : "+kapasitet +"\n";

                                this.rom = new Rom(romnummer,hus_id,beskrivelse,kapasitet,etasjenr);
                                ny.add(retur);
                            }
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
        protected void onPostExecute(ArrayList<String> ss) {
            //oppdaterer liste
            System.out.println("jaaa "+ss.size()+ss);
            setListe(ss);
        }

    }
}
