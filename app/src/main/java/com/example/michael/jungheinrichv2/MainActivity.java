package com.example.michael.jungheinrichv2;

import android.app.LauncherActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

        private ArrayAdapter<String> adapter;
        private LinkedList<String> list;
        private ListView lview;
        private EditText search;
        private String[] items;
        private int length;

        private ReportClient client;



        @Override
    protected void onCreate(Bundle savedInstanceState) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarstart);
        toolbar.setNavigationIcon(R.drawable.ic_logout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        list = new LinkedList<>();
        lview = findViewById(R.id.ListView);
        search = findViewById(R.id.searchtxt);

        client = new ReportClient();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                length = charSequence.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")) {
                    initList();
                }
                else {
                    search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() < length) {
                    initList();
                    search(editable.toString());
                }
            }
        });


            try {
                //   access = new DBAccess();
                //    list = access.getTables();
                initList();

            } catch (Exception ex) {
                for (int i = 1; i <= 20; i++) {
                    list.add(("TestItem" + i));

                }

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT);
            }


            if (list == null) {
                list = new LinkedList<>();
                for (int i = 1; i <= 20; i++) {
                    list.add(("TestItem" + i));

                }
            }

        try {

            //lview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch(Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT);
        }

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
              Intent intent = new Intent(MainActivity.this, TabActivity.class);
              Bundle b = new Bundle();
              b.putString("Report", (String)parent.getItemAtPosition(position));
              intent.putExtra("Report", (String)parent.getItemAtPosition(position));
              startActivity(intent);
            }
            });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            System.out.println("Logout!!");
        }
        else if(id == R.id.configuration)
        {
            System.out.println("ConfigFenster aufrufen!");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarstart);
        ImageView logo = (ImageView) findViewById(R.id.logoJungheinrich);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        logo.setX(offset);
    }

    public void initList() {
            client.run();
            list = client.getList();
            items = new String[list.size()];
            for(int i=0; i<list.size(); i++)
            {
                items[i] = list.get(i);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
            lview.setAdapter(adapter);
    }

    public void search(String searchtxt) {
            for(String item : items) {
                if(!item.toLowerCase().contains(searchtxt.toLowerCase())) {
                    System.out.println(item+ " gelöscht!");
                    list.remove(item);
                }
            }
            adapter.notifyDataSetChanged();
    }


}
