package com.example.michael.jungheinrichv2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login2);
        Button bt;

        bt = findViewById(R.id.button1);
        bt.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.dbdata.add("reimab14");
        MainActivity.dbdata.add("reimab14");
        MainActivity.dbdata.add("db2.htl-kaindorf.at");
        MainActivity.dbdata.add("1521");
        MainActivity.dbdata.add("orcl");

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("ONDESTROY aufgerufen!");

        saveData();

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
        try {
            System.out.println("Loaddata aufgerufen");
            FileInputStream fileInputStream = openFileInput("savedata");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));


            int i;
            String s_temp="";
            while( (i = fileInputStream.read()) != -1) {
                s_temp = s_temp + Character.toString((char)i);
            }
            System.out.println("Neu: "+s_temp);

            String[] line = s_temp.split(";");

            int size = MainActivity.dbdata.size();
            MainActivity.dbdata.clear();

            for (int f = 0; f<size; f++) {

                MainActivity.dbdata.add(f, line[f]);

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


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("PersNr", persNr);
                intent.putExtra("PersNr", persNr);
                startActivity(intent);


    }
}
