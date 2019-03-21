package com.example.michael.jungheinrichv2;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class TabActivity extends AppCompatActivity {

    public static TableClient client;
    private String[] colNames;
    private LinkedList<ArrayList<String>> content;
    private ArrayList<Integer> numbers;
    private boolean unconfirmed = true;
    public String item;
    private String statement;

    LinkedList<String> filter;
    LinkedList<TextView> textViews;
    //LinkedList<EditText> editTexts;




    private SQLParser parser;
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

        //get data from DB
        Bundle b = getIntent().getExtras();
        item = b.getString("Report");


        client = new TableClient();
        content = new LinkedList<>();

        if(!(item == "" || item == null))
        {


        client.setReport(item);
        client.receiveStatement();
        statement = client.getStatement();
        }
        else
        {
            statement = b.getString("Statement");
        }
        parser = new SQLParser(statement);
        System.out.println(statement);
        textViews = new LinkedList<>();

        if(parser.parseStatement())
        {
            Intent intent = new Intent(TabActivity.this, FilterActivity.class);

            intent.putExtra("Statement", statement);
            startActivity(intent);
        }


            setContentView(R.layout.activity_tab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());





        //editTexts = new LinkedList<>();







        //

        this.runClient();

        //

    }

    public void runClient()
    {
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

            if(numbers.size()==0)
            {
                System.out.println("keine Zahl enthalten!");
                tabLayout.removeTabAt(1);
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