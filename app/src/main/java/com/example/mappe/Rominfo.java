package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Arrays;

public class Rominfo extends AppCompatActivity {
    //initaliserer data
    String husid, romid, husnavn = "";
    TextView adresse, romnummeret, kapasitet, beskrivelse, etasjenummer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rominfo);
        //henter data
        husid = getIntent().getStringExtra("husid");
        romid = getIntent().getStringExtra("romid");
        husnavn = getIntent().getStringExtra("husnavn");
        adresse = (TextView) findViewById(R.id.husadresse);
        romnummeret = (TextView) findViewById(R.id.romnummer);
        kapasitet = (TextView) findViewById(R.id.kapasitet);
        beskrivelse = (TextView) findViewById(R.id.beskrivelse);
        etasjenummer = (TextView) findViewById(R.id.etasjer);

        //asynctask med rom
        RomJSON task = new RomJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romout.php"});
    }
    protected class RomJSON extends AsyncTask<String, Void, Rom> {
        JSONObject jsonObject;
        @Override
        protected Rom doInBackground(String... urls) {
            Rom rom = new Rom();
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
                    try{
                        JSONArray romobjekter = new JSONArray(output);
                        for (int i = 0; i < romobjekter.length(); i++) {
                            JSONObject jsonobject= romobjekter.getJSONObject(i);

                            int hus_id= jsonobject.getInt("hus_id");
                            int romnumer = jsonobject.getInt("romnummer");
                            String beskrivels= jsonobject.getString("beskrivelse");
                            String kapasit= jsonobject.getString("kapasitet");
                            String etasjenr= jsonobject.getString("etasjenr");
                            int nokkel = jsonobject.getInt("rom_id");
                            String id = husid+""+romid;
                            //System.out.println("bbbb"+id + "og"+nokkel);

                            //dersom primær nøkkelen er lik så henter vi dens info
                            if(id.equals(String.valueOf(nokkel))) {
                                rom.setBeskrivelse(beskrivels);
                                rom.setEtasjenr(etasjenr);
                                rom.setRomnummer(romnumer);
                                rom.setKapasitet(kapasit);
                            }
                        }
                        return rom;
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } catch(Exception e) {
                    return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Rom ss) {
            //setter textviewet sine verdier
            adresse.setText(husnavn);
            romnummeret.setText(String.valueOf(ss.getRomnummer()));
            etasjenummer.setText(ss.getEtasjenr());
            kapasitet.setText(ss.getKapasitet());
            beskrivelse.setText(ss.getBeskrivelse());
        }
    }

}
