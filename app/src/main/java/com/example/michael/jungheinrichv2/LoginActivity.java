package com.example.michael.jungheinrichv2;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> IDs;
    private LoginClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_login2);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarlogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button bt;

        bt = findViewById(R.id.button1);
        bt.setOnClickListener(this);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarlogin);
        ImageView logo = (ImageView) findViewById(R.id.logoJungheinrich);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        logo.setX(offset);
    }

    @Override
    protected void onStart() {
        //loginClient.setAnfrage("loaddata");
        System.out.println("ONSTART aufgerufen!");
        client = new LoginClient();
        if(MainActivity.dbdata.size()==0)
        {
            client.setAnfrage("loaddata");
            client.run();
            String data = client.getResult();
            String[] result = data.split(";");
            for(int i=0; i<result.length; i++)
            {
                MainActivity.dbdata.add(result[i]);
            }

            for (String s : MainActivity.dbdata) {
                System.out.println(s);
            }
        }
        //loginClient.run();
        //loadData();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        client.setAnfrage("savedata");
        System.out.println("ONDESTROY aufgerufen!");
        client.run();
        //saveData();
        super.onDestroy();
    }

    public void saveData() {
        try {

            FileOutputStream fOut = openFileOutput("savedata", Context.MODE_PRIVATE);
            //System.out.println(MainActivity.dbdata.get(0));
            String ergebnis = "";
            for (int i = 0; i<MainActivity.dbdata.size(); i++) {
                if (i == 1) {
                    String encrypted = "";
                    String sourceStr = MainActivity.dbdata.get(i);
                    try {
                        encrypted = AESUtils.encrypt(sourceStr);
                        System.out.println("Verschlüsselt: " + encrypted);
                        encrypted+=";";
                        ergebnis += encrypted;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String s = MainActivity.dbdata.get(i)+";";
                    ergebnis += s;
                }

            }
            System.out.println(ergebnis);
            fOut.write(ergebnis.getBytes());
            fOut.close();
            System.out.println("Daten wurden gespeichert");
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void loadData() {
        String daten = "";
        try {
            System.out.println("Loaddata aufgerufen");
            FileInputStream fileInputStream = openFileInput("savedata");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            daten = reader.readLine();
            System.out.println(daten);

            String[] line = daten.split(";");

            for (int f = 0; f<line.length; f++) {
                MainActivity.dbdata.add(line[f]);
            }

            String encrypted = line[1];
            System.out.println("encrypted: "+encrypted);
            String decrypted = "";
            try {
                decrypted = AESUtils.decrypt(encrypted);
                System.out.println("decrypted:" +decrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainActivity.dbdata.set(1, decrypted);

            for (String s : MainActivity.dbdata) {
                System.out.println(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        EditText et = findViewById(R.id.editText);
        String persNr = et.getText().toString();
        if(persNr.equals(""))
        {
            et.setError("Bitte eine Personalnummer eingeben!");
        }
        else {
            client.setAnfrage("checkPersNr;" + persNr);
            client.run();
            String pruef = client.getResult();
            System.out.println("pruef: "+pruef);
            if (pruef.equals("ok")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("PersNr", persNr);
                intent.putExtra("PersNr", persNr);
                startActivity(intent);
            } else et.setError("Ungültige Personalnummer!");
        }
    }
}
