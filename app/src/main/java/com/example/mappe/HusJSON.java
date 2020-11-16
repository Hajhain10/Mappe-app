package com.example.mappe;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HusJSON extends AsyncTask<String, Void,String> {
    JSONObject jsonObject;
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
                try{
                    JSONArray mat = new JSONArray(output);
                    for (int i = 0; i < mat.length(); i++) {
                        JSONObject jsonobject= mat.getJSONObject(i);
                                /*int romnummer = jsonobject.getInt("romnummer");
                                String dato= jsonobject.getString("dato");
                                String starttid= jsonobject.getString("starttid");
                                String slutttid= jsonobject.getString("slutttid");
                                retur = retur + romnummer +" "+dato +" "+starttid +" "+slutttid +"\n";*/
                                /*
                        int romnummer = jsonobject.getInt("romnummer");
                        int hus_id= jsonobject.getInt("hus_id");
                        String beskrivelse= jsonobject.getString("beskrivelse");
                        String kapasitet= jsonobject.getString("kapasitet");
                        String etasjenr= jsonobject.getString("etasjenr");
                        retur = retur + romnummer +" "+hus_id +" "+beskrivelse+" "+kapasitet +" "+etasjenr +"\n";

                                 */
                                String beskrivelse = jsonobject.getString("beskrivelse");
                                String gateadresse= jsonobject.getString("gateadresse");
                                String koordinater= jsonobject.getString("koordinater");
                                String antalletasjer= jsonobject.getString("antalletasjer");
                                retur = retur + beskrivelse +" "+gateadresse +" "+koordinater +
                                        " "+antalletasjer +"\n";
                        System.out.println("ccccccc "+retur );
                    }
                    return retur;
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                return retur;
            } catch(Exception e) {
                return"Noe gikk feil";
            }
        }
        return retur;
    }
    @Override
    protected void onPostExecute(String ss) {
        System.out.print("bbbbbbb "+ss);
    }
}

