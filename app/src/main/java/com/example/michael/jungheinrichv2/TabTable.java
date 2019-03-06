package com.example.michael.jungheinrichv2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabTable extends Fragment {

    private TableClient client;
    private LinkedList<ArrayList<String>> content;
    private String[] colNames;
    private static String TAG = "Tab1";

    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica", "Mohammed", "Kelsey", "Sam", "Robert", "Ashley"};
    PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableLayout table = getView().findViewById(R.id.table);

        TableRow row = new TableRow(this.getContext());
        TableRow.LayoutParams layout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        layout.setMargins(100, 0, 100, 0);
        table.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        ScrollView sv = getView().findViewById(R.id.sv);
        //sv.setBackgroundColor(Color.BLACK);

        content = new LinkedList<>();
        Bundle b = getArguments();
        //        this.getContext().getIntent().getExtras();
        colNames = b.getStringArray("ColNames");


        for(int i = 1; i <= (int) b.get("ListSize"); i++)
        {
            content.add(b.getStringArrayList("Record"+i));
        }


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



        TextView tv;
        row = new TableRow(this.getContext());
        for (int i = 0; i < colNames.length; i++) {
            tv = new TextView(this.getContext());
            tv.setText(colNames[i]);
            tv.setTextSize(25);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(tv, layout);
        }
        table.addView(row);


        row = new TableRow(this.getContext());

        for (ArrayList<String> list : content) {
            for (int i = 0; i < list.size(); i++) {
                tv = new TextView(this.getContext());
                tv.setText(list.get(i));
                tv.setTextSize(20);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                row.addView(tv, layout);
            }

            table.addView(row);
            row = new TableRow(this.getContext());


        }
    }
        private void addDataSet()
        {
            Log.d(TAG, "addDataSet started");
            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i], i));
            }

            for (int i = 0; i < xData.length; i++) {
                xEntrys.add(xData[i]);
            }

            PieDataSet pieDataSet = new PieDataSet(yEntrys, "Employee Sales");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(12);

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.GRAY);
            colors.add(Color.BLUE);
            colors.add(Color.RED);
            colors.add(Color.GREEN);
            colors.add(Color.CYAN);
            colors.add(Color.YELLOW);
            colors.add(Color.MAGENTA);

            pieDataSet.setColors(colors);

            Legend legend = pieChart.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();
        }
    }

