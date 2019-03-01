package com.example.michael.jungheinrichv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class KonfigActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfig);
        Button bt;
        bt = findViewById(R.id.button);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText et = findViewById(R.id.editBenutzer);
        EditText et1 = findViewById(R.id.editPasswort);
        EditText et2 = findViewById(R.id.editHostname);
        EditText et3 = findViewById(R.id.editPort);
        String benutzer = et.getText().toString();
        String passwort = et1.getText().toString();
        String hostname = et2.getText().toString();
        String port = et3.getText().toString();

        Intent intent = new Intent(KonfigActivity.this, MainActivity.class);
        Bundle b = new Bundle();



        startActivity(intent);


    }
}
