package com.example.michael.jungheinrichv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class KonfigActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarkonfig);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        EditText benutzer = findViewById(R.id.editBenutzer);
        EditText passwort = findViewById(R.id.editPasswort);
        EditText hostname = findViewById(R.id.editHostname);
        EditText port = findViewById(R.id.editPort);
        EditText sid = findViewById(R.id.editSID);
        benutzer.setText(MainActivity.dbdata.get(0));
        passwort.setText(MainActivity.dbdata.get(1));
        hostname.setText(MainActivity.dbdata.get(2));
        port.setText(MainActivity.dbdata.get(3));
        sid.setText(MainActivity.dbdata.get(4));
        Button bt = findViewById(R.id.button);
        bt.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarkonfig);
        ImageView logo = (ImageView) findViewById(R.id.logoJungheinrich);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        logo.setX(offset);
    }

    @Override
    public void onClick(View v) {
        EditText benutzer = findViewById(R.id.editBenutzer);
        EditText passwort = findViewById(R.id.editPasswort);
        EditText hostname = findViewById(R.id.editHostname);
        EditText port = findViewById(R.id.editPort);
        EditText sid = findViewById(R.id.editSID);
        if(!benutzer.getText().toString().equals(MainActivity.dbdata.get(0))) {
            MainActivity.dbdata.set(0, benutzer.getText().toString());
            MainActivity.hasChanged = true;
        }
        if(!passwort.getText().toString().equals(MainActivity.dbdata.get(1))) {
            MainActivity.dbdata.set(1, passwort.getText().toString());
            MainActivity.hasChanged = true;
        }
        if(!hostname.getText().toString().equals(MainActivity.dbdata.get(2))) {
            MainActivity.dbdata.set(2, hostname.getText().toString());
            MainActivity.hasChanged = true;
        }
        if(!port.getText().toString().equals(MainActivity.dbdata.get(3))) {
            MainActivity.dbdata.set(3, port.getText().toString());
            MainActivity.hasChanged = true;
        }
        if(!sid.getText().toString().equals(MainActivity.dbdata.get(4))) {
            MainActivity.dbdata.set(4, sid.getText().toString());
            MainActivity.hasChanged = true;
        }

        Intent intent = new Intent(KonfigActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
