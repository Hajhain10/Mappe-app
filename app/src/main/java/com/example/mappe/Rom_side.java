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
    ListView lv;
    List<String> liste = new ArrayList<>();
    String id, romnavn = "";
    String idhus, husnavn="";
    Button rom;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rom_side);
        idhus = getIntent().getStringExtra("hus_id");
        husnavn = getIntent().getStringExtra("husnavn");
        lv = (ListView)findViewById(R.id.personer);
        rom = (Button) findViewById(R.id.reserverRom);
        rom.setVisibility(View.GONE);
        RomJSON task = new RomJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romout.php"});
    }
    protected void setListe(ArrayList<String> listen){
        //listen som viser arrayadapterens verdier
        liste = listen;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liste);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String trakk = adapter.getItem(i);
                    String[] lb = trakk.split(" ");
                    id = lb[1];
                    rom.setVisibility(View.VISIBLE);
                    System.out.println("mmmmm " + id + "," + idhus);
                }
            });
        }

    public void LagReservsjon(View view) {
        if (!id.equals("")) {
            Intent i = new Intent(this, Reservasjon.class);
            i.putExtra("idhus", idhus);
            i.putExtra("idrom", id);
            i.putExtra("husnavn", husnavn);
            i.putExtra("romnavn", romnavn);
            startActivity(i);
            //BYTT SIDE OG SEND MED HUSID OG ROMID
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Vennligst velg et rom", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void leggInnNyttrom(View view) {

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
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject= mat.getJSONObject(i);

                            int hus_id= jsonobject.getInt("hus_id");
                            if(idhus.equals(String.valueOf(hus_id))) {
                                int romnummer = jsonobject.getInt("romnummer");
                                String beskrivelse= jsonobject.getString("beskrivelse");
                                String kapasitet= jsonobject.getString("kapasitet");
                                String etasjenr= jsonobject.getString("etasjenr");

                                retur =  "romnummer "+romnummer +" "+hus_id +" "+beskrivelse+" "+kapasitet +" "+etasjenr +"\n";

                                rom = new Rom(romnummer,hus_id,beskrivelse,kapasitet,etasjenr);
                                ny.add(retur);
                            }
                            System.out.println("ccccccc "+retur );
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
            System.out.println("jaaa "+ss.size()+ss);
            setListe(ss);
        }

    }
}
