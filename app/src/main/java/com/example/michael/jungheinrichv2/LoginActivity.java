package com.example.michael.jungheinrichv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> IDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login2);
        Button bt;
        IDs = new ArrayList<>();
        IDs.add("1234");
        IDs.add("4321");
        IDs.add("9876");
        IDs.add("6789");
        bt = findViewById(R.id.button1);
        bt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        EditText et = findViewById(R.id.editText);
        String persNr = et.getText().toString();
        for (int i = 0; i<IDs.size(); i++) {
            if (persNr.equals(IDs.get(i))) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            } else {
                Toast.makeText(getApplicationContext(),"Ungültige Personalnummer", Toast.LENGTH_LONG).show();
            }
        }
    }
}
