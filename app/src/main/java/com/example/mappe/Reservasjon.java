package com.example.mappe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Reservasjon extends AppCompatActivity {
    TextView textView;
    EditText skrivinn;
    String idhus,idrom,husnavn="";
    ListView lv;
    ArrayList<String> liste = new ArrayList<>();
    String datoen="20.11.20";
    ArrayList<Integer> valgtetider = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyreservasjon);
        lv = (ListView)findViewById(R.id.tider);
        textView = (TextView) findViewById(R.id.prøv);
        skrivinn = (EditText) findViewById(R.id.dato);
        idhus = getIntent().getStringExtra("idhus");
        idrom=getIntent().getStringExtra("idrom");
        husnavn = getIntent().getStringExtra("husnavn");

        skrivinn.setText(datoen);liste.add("13.00 - 13.30");liste.add("13.30 - 14.00");
        liste.add("14.00 - 14.30");liste.add("14.30 - 15.00");liste.add("15.00 - 15.30");
        liste.add("15.30 - 16.00");liste.add("16.00 - 16.30");
        liste.add("16.30 - 17.00");liste.add("17.00 - 17.30");
        liste.add("17.30 - 18.00");liste.add("18.00 - 18.30");liste.add("18.30 - 19.00");
        liste.add("19.00 - 19.30");
        HuskJSON jsson = new HuskJSON();
        jsson.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romreservasjonout.php"});

       // double latitude = getIntent().getExtras().getDouble("lat",0);
    }
    protected void setListe(ArrayList<String> listen){
        //listen som viser arrayadapterens verdier
        liste = listen;
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,liste);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long trakk = adapter.getItemId(i);
                if(lv.isItemChecked(i)){
                lv.setItemChecked(i,true);
                valgtetider.add(i);

            }else {
                    lv.setItemChecked(i, false);
                   valgtetider.remove(valgtetider.indexOf(i));
                }
                System.out.println("tider som er valngt "+ Arrays.toString(valgtetider.toArray()));
            }
        });

    }

    public void oppdaterlisten(View view) {
        datoen = skrivinn.getText().toString();
        liste.clear();liste.add("13.00 - 13.30");liste.add("13.30 - 14.00");
        liste.add("14.00 - 14.30");liste.add("14.30 - 15.00");liste.add("15.00 - 15.30");
        liste.add("15.30 - 16.00");liste.add("16.00 - 16.30");
        liste.add("16.30 - 17.00");liste.add("17.00 - 17.30");
        liste.add("17.30 - 18.00");liste.add("18.00 - 18.30");liste.add("18.30 - 19.00");
        HuskJSON jsson = new HuskJSON();
        jsson.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romreservasjonout.php"});
    }

    public void klargjørliste(View view) {
        System.out.println("tider "+Arrays.toString(valgtetider.toArray()));
        String tid = lagnyetider();
        Intent i = new Intent(this,ReserverRom.class);
        i.putExtra("tider",tid);
        i.putExtra("idhus",idhus);
        i.putExtra("idrom",idrom);
        i.putExtra("dato",datoen);
        i.putExtra("husnavn",husnavn);
        startActivity(i);
    }

    private String lagnyetider() {
        Collections.sort(valgtetider);
        StringBuilder ut = new StringBuilder();
        ut.append(liste.get(valgtetider.get(0)).substring(0,5));
        int k = 0;
        for(int i = 0; i<valgtetider.size()-1;i++){
           // k = valgtetider.get(i);
            //System.out.println("oooo"+liste.get(valgtetider.get(i)).substring(8)+ "...."+liste.get(valgtetider.get(i+1)).substring(0,5));
            if(!liste.get(valgtetider.get(i)).substring(8).equals(liste.get(valgtetider.get(i+1)).substring(0,5))){
                ut.append(" "+liste.get(valgtetider.get(i)).substring(8)+",");
                ut.append(liste.get(valgtetider.get(i+1)).substring(0,5));
            }
        }
        ut.append(" "+liste.get(valgtetider.get(valgtetider.size()-1)).substring(8));
        System.out.println("oooo"+ut.toString());
        return ut.toString();
    }

    protected class HuskJSON extends AsyncTask<String, Void, ArrayList<String>> {
        JSONObject jsonObject;
        ArrayList<Romreservasjon> ny = new ArrayList<>();
        Romreservasjon etHus = new Romreservasjon();
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
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
                            String dato= jsonobject.getString("dato");
                            System.out.println("mmmm"+dato);
                            if (datoen.equals(dato)) {
                                int romnummer = jsonobject.getInt("romnummer");
                                int husId = jsonobject.getInt("hus_id");
                                String starttid= jsonobject.getString("starttid");
                                String slutttid= jsonobject.getString("slutttid");
                                tavekktider(starttid,slutttid);
                               // retur = retur + romnummer +" "+dato +" "+starttid +" "+slutttid +"\n";
                                //etHus = new Romreservasjon(romnummer,husId,dato,starttid,slutttid);
                                //ny.add(etHus);
                                //System.out.println("ccccccc " + retur);
                            }
                        }
                        return liste;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return liste;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        private void tavekktider(String startid, String slutttid) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(startid);
            Calendar k = Calendar.getInstance();
            k.set(Calendar.MINUTE, Integer.parseInt(stringBuilder.subSequence(3,5).toString()));
            k.set(Calendar.HOUR_OF_DAY,Integer.parseInt(stringBuilder.subSequence(0,2).toString()));
            String tiden =startid;
            if (tiden.length()==4){tiden+="0";}
            int indeks=-1;
            for (int i = 0; i<liste.size();i++){
                if(liste.get(i).contains(startid)){
                    indeks = i;
                    System.out.println("mmm nullte indeks"+indeks);
                }
            }
            System.out.println("mmm"+slutttid);
            while (!tiden.equals(slutttid)){
                System.out.println("mmm"+tiden+ tiden.length());
                System.out.println("indeks"+indeks);

                liste.remove(indeks);//indeks++;
                k.add(Calendar.MINUTE,30);
                tiden = k.get(Calendar.HOUR_OF_DAY)+"."+k.get(Calendar.MINUTE);
                if (tiden.length()==4){tiden+="0";}
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> ss) {
            String ut = "";
            setListe(ss);
            textView.setText(Arrays.toString(ss.toArray()));
        }
    }
}
