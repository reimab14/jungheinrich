package com.example.michael.jungheinrichv2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.Cipher;
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
            for (String s : MainActivity.dbdata) {
                fOut.write(s.getBytes());
                System.out.println(s+";");

            }

            byte[] key = ("secretkey").getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secret = new SecretKeySpec(key, "AES");

            for (int i = 0; i<MainActivity.dbdata.size(); i++) {
               if (i==1) {
                   Cipher cipher = null;
                   cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                   cipher.init(Cipher.ENCRYPT_MODE, secret);
                   byte[] cipherText = cipher.doFinal(MainActivity.dbdata.get(1).getBytes("UTF-8"));
                   fOut.write(MainActivity.dbdata.get(i).getBytes());
               }
               else {
                   fOut.write(MainActivity.dbdata.get(i).getBytes());
               }
            }
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

            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            System.out.println(sb.toString());
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
