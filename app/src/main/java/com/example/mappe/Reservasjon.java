package com.example.mappe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

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

public class Reservasjon extends AppCompatActivity {
    TextView textView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyreservasjon);
        textView = (TextView) findViewById(R.id.prøv);
        HuskJSON jsson = new HuskJSON();
        jsson.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romreservasjonout.php"});

       // double latitude = getIntent().getExtras().getDouble("lat",0);

    }
    protected class HuskJSON extends AsyncTask<String, Void, ArrayList<Romreservasjon>> {
        JSONObject jsonObject;
        ArrayList<Romreservasjon> ny = new ArrayList<>();
        Romreservasjon etHus = new Romreservasjon();
        @Override
        protected ArrayList<Romreservasjon> doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject= mat.getJSONObject(i);
                                int romnummer = jsonobject.getInt("romnummer");
                                int husId = jsonobject.getInt("hus_id");
                                String dato= jsonobject.getString("dato");
                                String starttid= jsonobject.getString("starttid");
                                String slutttid= jsonobject.getString("slutttid");
                                retur = retur + romnummer +" "+dato +" "+starttid +" "+slutttid +"\n";

                           etHus = new Romreservasjon(romnummer,husId,dato,starttid,slutttid);
                            ny.add(etHus);
                            System.out.println("ccccccc " + retur);
                        }
                        return ny;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return ny;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Romreservasjon> ss) {
            String ut = "";
            for (int i =0; i< ss.size(); i++){
                ut+=ss.get(i).getHusID() +ss.get(i).getRomnummer() +ss.get(i).getDato()
                        +ss.get(i).getSlutttid() + ss.get(i).getStarttid();
            }
            textView.setText(Arrays.toString(ss.toArray()));
        }
    }
}
