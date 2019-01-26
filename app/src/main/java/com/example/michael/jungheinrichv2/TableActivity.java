package com.example.michael.jungheinrichv2;

import android.graphics.Color;
import android.graphics.Path;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;

public class TableActivity extends AppCompatActivity
{
    private  TableClient client;
    private LinkedList<ArrayList<String>> content;
    private String[] colNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        //String rep = (String) savedInstanceState.get("Report");

        TableLayout table = findViewById(R.id.table);
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        layout.setMargins(100,0,100,0);
        table.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        ScrollView sv = findViewById(R.id.sv);
        //sv.setBackgroundColor(Color.BLACK);
        Bundle b = getIntent().getExtras();
        String item = b.getString("Report");
        row = new TableRow(this);
        //TextView view = new TextView(this);
        //view.setText("Item:"+item);
        //view.setTextSize(20);
        //row.addView(view,layout);
        //view = new TextView(this);
        //view.setText("Column1");
        //view.setTextSize(20);
        //row.addView(view,layout);
        //view = new TextView(this);
        //view.setText("Column2");
        //view.setTextSize(20);
        //row.addView(view,layout);
        //view = new TextView(this);
        //view.setText("Column3");
        //view.setTextSize(20);
        //row.addView(view,layout);
        //table.addView(row);
        //TableLayout.LayoutParams tablayout =

        client = new TableClient();
        content = new LinkedList<>();
        client.setStatement(item);
        client.run();
        colNames = client.getColNames().split(";");
        content = client.getList();

        TextView tv;
        row = new TableRow(this);
        for(int i = 0; i < colNames.length; i++)
        {
            tv = new TextView(this);
            tv.setText(colNames[i]);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(tv, layout);
        }
        table.addView(row);


        row = new TableRow(this);

        for(ArrayList<String> list : content)
        {
            for(int i = 0; i < list.size(); i++) {
                tv = new TextView(this);
                tv.setText(list.get(i));
                tv.setTextSize(12);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                row.addView(tv, layout);
            }

                table.addView(row);
                row = new TableRow(this);



        }
    }
}
