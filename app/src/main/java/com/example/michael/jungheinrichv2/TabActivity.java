package com.example.michael.jungheinrichv2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class TabActivity extends AppCompatActivity {

    private TableClient client;
    private String[] colNames;
    private LinkedList<ArrayList<String>> content;
    private ArrayList<Integer> numbers;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        //get data from DB
        Bundle b = getIntent().getExtras();
        String item = b.getString("Report");

        client = new TableClient();
        content = new LinkedList<>();
        client.setReport(item);
        client.receiveStatement();
        client.run();
        colNames = client.getColNames().split(";");
        content = client.getList();
        numbers = new ArrayList<>();
        System.out.println("Größe: "+content.size());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(content.size()>1)
        {
            System.out.println("mehr als ein Eintrag!");
            tabLayout.removeTabAt(1);
        }
        else {
            System.out.println("Data: ");
            for(int j=0; j<content.get(0).size(); j++)
            {
                System.out.println(content.get(0).get(j));
                String search = content.get(0).get(j).toString();
                System.out.println("Zahl: "+search.matches("-?\\d+(\\.\\d+)?"));
                if(search.matches("-?\\d+(\\.\\d+)?")) {
                    numbers.add(j);
                }
            }

            System.out.println("Spalten:");
            for(int a=0; a<numbers.size(); a++)
            {
                System.out.println(content.get(0).get(numbers.get(a)).toString()+": "+colNames[numbers.get(a)].toString());
            }
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView logo = (ImageView) findViewById(R.id.logoJungheinrich);
        int offset = (toolbar.getWidth() / 2) - (logo.getWidth() / 2);
        logo.setX(offset);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new TabTable();
                    break;
                case 1:
                    fragment = new ChartSwitch();
                    break;
            }
            Bundle b = new Bundle();
            b.putCharSequenceArray("ColNames", colNames);

            for(int i = 1; i <= content.size(); i++)
            {
                b.putStringArrayList("Record"+i, content.get(i-1));
            }

            b.putInt("ListSize", content.size());
            b.putIntegerArrayList("Numbers", numbers);


            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0:
                    return "Tabelle";
                case 1:
                    return "Diagramme";
            }
            return null;
        }
    }
}