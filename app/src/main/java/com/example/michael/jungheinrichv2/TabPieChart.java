package com.example.michael.jungheinrichv2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TabPieChart extends Fragment {

    private static String TAG = "Tab1";
    private LinkedList<ArrayList<String>> content;
    private String[] colNames;
    private ArrayList<Integer> numbers;
    private int[] yData;
    private String[] xData;
    PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_piechart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onCreate: starting to create chart");

        pieChart = (PieChart) getView().findViewById(R.id.idPieChart);

        content = new LinkedList<>();
        Bundle b = getArguments();

        colNames = b.getStringArray("ColNames");

        for(int i = 1; i <= (int) b.get("ListSize"); i++)
        {
            content.add(b.getStringArrayList("Record"+i));
        }

        numbers = b.getIntegerArrayList("Numbers");
        yData = new int[numbers.size()];
        xData = new String[numbers.size()];

        for(int a=0; a<numbers.size(); a++)
        {
            yData[a] = Integer.parseInt(content.get(0).get(numbers.get(a)).toString());
            xData[a] = colNames[numbers.get(a)].toString();
        }


        pieChart.setRotationEnabled(false);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Jungheinrich Chart");
        pieChart.setCenterTextSize(10);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setDrawEntryLabels(true);

        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: "+e.toString());
                Log.d(TAG, "onValueSelected: "+h.toString());

                int pos1 = e.toString().indexOf("y: ");
                String ammount = e.toString().substring(pos1 + 3);

                for(int i=0; i<yData.length; i++)
                {
                    if(yData[i] == Double.parseDouble(ammount))
                    {
                        pos1 = i;
                        break;
                    }
                }
                String clicked = xData[pos1];
                Toast.makeText(getActivity(),clicked+": "+ammount, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i=0; i<yData.length; i++)
        {
            yEntrys.add(new PieEntry(yData[i],i));
        }

        for(int i=0; i<xData.length; i++)
        {
            xEntrys.add(xData[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
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
        List<LegendEntry> legendEntries = new ArrayList<>();

        for(int i=0; i<yEntrys.size(); i++)
        {
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.formColor = colors.get(i);
            legendEntry.label = xEntrys.get(i);
            legendEntries.add(legendEntry);
        }
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setCustom(legendEntries);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
