package com.example.mappe;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Reservasjon extends AppCompatActivity {
    //initasliserer data
    TextView skrivinn;
    DatePickerDialog velgdato;
    String idhus,idrom,husnavn="";
    ListView lv;
    ArrayList<String> liste =  new ArrayList<>();
    String datoen="";
    ArrayList<Integer> valgtetider = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nyreservasjon);
        lv = (ListView)findViewById(R.id.tider);
        skrivinn = (TextView) findViewById(R.id.dato);
        //henter data
        idhus = getIntent().getStringExtra("idhus");
        idrom=getIntent().getStringExtra("idrom");
        husnavn = getIntent().getStringExtra("husnavn");
        //henter dato for i dag
        Date idag = new Date();
        SimpleDateFormat dato = new SimpleDateFormat("dd.MM.yyyy");
        datoen = dato.format(idag);
        skrivinn.setText("Dato valgt "+datoen+", Trykk her for å endre dato");
        //datoene som skal være tilgjengelige
        liste = new ArrayList<>();
        Collections.addAll(liste,getResources().getStringArray(R.array.timeliste));

        //henter tider som ikke er ledige
        HuskJSON jsson = new HuskJSON();
        jsson.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romreservasjonout.php"});

       // double latitude = getIntent().getExtras().getDouble("lat",0);
    }
    protected void setListe(ArrayList<String> listen){
        //liste som oppdaterer listen
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

    public void oppdaterlisten() {
        //etter at datoen blir satt så oppdateres listen ved hjelp av asynctask
        liste = new ArrayList<>();
        Collections.addAll(liste,getResources().getStringArray(R.array.timeliste));
        HuskJSON jsson = new HuskJSON();
        jsson.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romreservasjonout.php"});
    }

    public void klargjørliste(View view) {
        //dersom ingenting er valgt
        if (valgtetider.size() == 0){
            Toast.makeText(this,"Du må velge tider",
                    Toast.LENGTH_SHORT).show();
        }else {
            //sender over data til neste side
            String tid = lagnyetider();
            Intent i = new Intent(this,ReserverRom.class);
            i.putExtra("tider",tid);
            i.putExtra("idhus",idhus);
            i.putExtra("idrom",idrom);
            i.putExtra("dato",datoen);
            i.putExtra("husnavn",husnavn);
            startActivity(i);
            finish();
        }
    }

    private String lagnyetider() {
        //gjør om tider som er valgt for legge inn så få ganger som mulig
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

    public void datovelger(View view) {
        //når tekstview blir trykket på og man skal velge dato
        Calendar kalender = Calendar.getInstance();
        int dag = kalender.get(Calendar.DAY_OF_MONTH);
        int maned = kalender.get(Calendar.MONTH);
        int aar = kalender.get(Calendar.YEAR);

        velgdato = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int aar, int maned, int dag) {
                        datoen = String.format("%02d",dag)+"."+String.format("%02d",(maned+1))+"."+aar;
                        skrivinn.setText("Dato valgt "+datoen+", Trykk her for å endre dato");
                        oppdaterlisten();
                    }
                }, aar, maned, dag);
        velgdato.show();
    }

    protected class HuskJSON extends AsyncTask<String, Void, ArrayList<String>> {
        JSONObject jsonObject;
        ArrayList<Romreservasjon> ny = new ArrayList<>();
        Romreservasjon etHus = new Romreservasjon();
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
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
                        //går igjennom rom for å finne tider som er opptatt
                        JSONArray romreservasjon = new JSONArray(output);
                        for (int i = 0; i < romreservasjon.length(); i++) {
                            JSONObject jsonobject= romreservasjon.getJSONObject(i);
                            int romnummer = jsonobject.getInt("romnummer");
                            int husId = jsonobject.getInt("hus_id");
                            String datok=jsonobject.getString("dato");
                            System.out.println("datoene idag"+ datok);
                            //dersom dato og rom er likt tar vi vekk tider som den er bestilt på
                            if (datoen.equals(datok) && String.valueOf(romnummer).equals(idrom)
                            && String.valueOf(husId).equals(idhus)) {
                                System.out.println("nå er jeg her"+idrom);
                                String starttid= jsonobject.getString("starttid");
                                System.out.println("startiida "+starttid);
                                String slutttid= jsonobject.getString("slutttid");
                                tavekktider(starttid,slutttid);
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
        //metode for å ta vekk tider som er opptatt
        //Gir du den to tider vil den ta vekk alt i mellom
        private void tavekktider(String startid, String slutttid) {
            System.out.println("kook"+startid+","+slutttid);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(startid);
            Calendar k = Calendar.getInstance();
            k.set(Calendar.MINUTE, Integer.parseInt(stringBuilder.subSequence(3,5).toString()));
            k.set(Calendar.HOUR_OF_DAY,Integer.parseInt(stringBuilder.subSequence(0,2).toString()));
            String tiden =startid;
            System.out.println("tiiden "+startid);
            //fikk 13.0 istedenfor 13.00 i blant så legger til en 0
            if (tiden.length()!=5){
                String[] tids = tiden.split(":");
                System.out.println("tiiden "+tids[0]+","+tids[1]+".,"+tids[0].length());
                if(tids[0].length() != 2){
                    tiden= "0"+tiden;
                }else if(tids[1].length() != 2){
                    tiden=tiden+"0";
                }
            }
            int indeks=-1;
            for (int i = 0; i<liste.size();i++){
                if(liste.get(i).contains(startid)){
                    indeks = i;
                }
            }
            System.out.println("mmm nullte indeks"+indeks);
            System.out.println("tiiden"+slutttid);
            while (!tiden.equals(slutttid)){
                System.out.println("mmm"+tiden+ tiden.length());
                System.out.println("indeks"+indeks);

                liste.remove(indeks);
                k.add(Calendar.MINUTE,30);
                tiden = k.get(Calendar.HOUR_OF_DAY)+":"+k.get(Calendar.MINUTE);
                System.out.println("tiiii "+tiden + tiden.length());
                if (tiden.length()!=5){
                    String[] tids = tiden.split(":");
                    if(tids[0].length() != 2){
                        tiden= "0"+tiden;
                    }else if(tids[1].length() != 2){
                        tiden=tiden+"0";
                    }
                    System.out.println("tiiden hh "+tiden);
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> ss) {
            //oppdaterer listen
            if(ss != null) {
                System.out.println(Arrays.toString(ss.toArray()));
                setListe(ss);
            }else {
                //denne skal ikke komme
                liste.clear();
                liste.add("en feil oppstod, prøv igjen senere");
                setListe(liste);
            }
        }
    }
    public void onResume(){
        super.onResume();
        //oppdateres hver gang man er i resume
        System.out.println("er i onresume");
        HuskJSON task = new HuskJSON();
        task.execute(new String[]{"http://student.cs.oslomet.no/~s331409/romout.php"});
    }
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("dato",datoen);
        outState.putIntegerArrayList("valgtetider",valgtetider);

    }
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        datoen = savedInstanceState.getString("dato");
        skrivinn.setText("Dato valgt "+datoen+", Trykk her for å endre dato");

    }
}
