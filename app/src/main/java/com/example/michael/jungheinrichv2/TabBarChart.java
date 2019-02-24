package com.example.michael.jungheinrichv2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabBarChart  extends Fragment {

    BarChart barChart;
    private LinkedList<ArrayList<String>> content;
    private String[] colNames;
    private ArrayList<Integer> numbers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_barchart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        barChart = (BarChart) getView().findViewById(R.id.bargraph);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        content = new LinkedList<>();
        Bundle b = getArguments();

        colNames = b.getStringArray("ColNames");

        for(int i = 1; i <= (int) b.get("ListSize"); i++)
        {
            content.add(b.getStringArrayList("Record"+i));
        }

        numbers = b.getIntegerArrayList("Numbers");

        for(int a=0; a<numbers.size(); a++)
        {
            barEntries.add(new BarEntry(a, Integer.parseInt(content.get(0).get(numbers.get(a)).toString())));
        }

        /*barEntries.add(new BarEntry(0,44f));
        barEntries.add(new BarEntry(1,88f));
        barEntries.add(new BarEntry(2,66f));
        barEntries.add(new BarEntry(3,12f));
        barEntries.add(new BarEntry(4,19f));
        barEntries.add(new BarEntry(5,91f));*/
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);

        barDataSet.setColors(colors);

        final ArrayList<String> theDates = new ArrayList<>();

        for(int a=0; a<numbers.size(); a++)
        {
            theDates.add(colNames[numbers.get(a)].toString());
        }
        /*theDates.add("April");
        theDates.add("May");
        theDates.add("June");
        theDates.add("July");
        theDates.add("August");
        theDates.add("September");*/

        BarData theData = new BarData(barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.getDescription().setEnabled(false);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return theDates.get((int)value);
            }
        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
    }
}
