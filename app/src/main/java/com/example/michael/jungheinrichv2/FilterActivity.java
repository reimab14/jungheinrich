package com.example.michael.jungheinrichv2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.LinkedList;

import static android.icu.lang.UCharacter.toUpperCase;

public class FilterActivity extends AppCompatActivity
{
    private String statement;
  //  private TableClient client;
    private SQLParser parser;
    private ListView lvtext;
    private ListView lvinput;
    private LinkedList<String> filter;
    //private CustomAdapter adapter;
    private LinkedList<String> krits;
    private Button confirm;
    private ArrayAdapter<TextView> adaptertext;
    private ArrayAdapter<EditText> adapterinput;
    private LinkedList<TextView> textlist;
    private LinkedList<EditText> inputlist;
    private TableLayout tableFilter;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        System.out.println("Filter Activity gestartet");
        confirm = findViewById(R.id.btConfirm);
        Bundle b = getIntent().getExtras();
        statement =  b.getString("Statement");
        System.out.println("Aktuelles Statement: "+statement);
        krits = new LinkedList<>();
       // lvtext = findViewById(R.id.listviewtext);
       // lvinput = findViewById(R.id.listviewinput);
       // textlist = new LinkedList<TextView>();
       // inputlist = new LinkedList<EditText>();
        parser = new SQLParser(statement);
        //adapter = new CustomAdapter(filter);
        filter = parser.getFilters(statement);
        TableRow.LayoutParams layout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
        tableFilter = findViewById(R.id.filterTable);
        System.out.println("Filterlänge: "+filter.size());
       // System.out.println("Adapter: "+adapter);

       // double width = getWindowManager().getDefaultDisplay().getWidth()/2;


        //CustomLayout layout;

        TextView tview;
        EditText edtext;
        TableRow row = new TableRow(this.getBaseContext());
        for(int i = 0; i < filter.size(); i++)
        {
            tview = new TextView(this.getBaseContext());
            tview.setText(filter.get(i));
            tview.setTextSize(20);
            row.addView(tview);

            edtext = new EditText(this.getBaseContext());
            edtext.setTextSize(20);
            row.addView(edtext);

            //layout = new CustomLayout(this);
            //layout.setFilter(filter.get(i));
            tableFilter.addView(row);
            row = new TableRow(this.getBaseContext());
        }

        //adaptertext = new ArrayAdapter<TextView>(this, android.R.layout.simple_list_item_1, android.R.id.text1, textlist);
        //adapterinput = new ArrayAdapter<EditText>(this, android.R.layout.simple_list_item_1, android.R.id.text1, inputlist);













        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                for (int i = 0; i < filter.size(); i++)
                {
                    TableRow row = (TableRow) tableFilter.getChildAt(i);
                    TextView tv = (TextView)row.getChildAt(0);
                    EditText et = (EditText) row.getChildAt(1);
                    String krit = ""+tv.getText()+";"+et.getText();
                    krit = krit.toUpperCase();
                    System.out.println("Krit: "+krit);

                   krits.add(krit);
                }

                boolean confirmed = true;
                for (int i = 0; i < krits.size(); i++)
                {
                    if(krits.get(i) == "" || krits.get(i) == null)
                    {

                        confirmed = false;

                    }
                }




                if(confirmed) {
                    parser.setAppKrits(krits);


                    statement = parser.getStatement();
                    System.out.println("Geparstes Statement: " + statement);


                    Intent intent = new Intent(FilterActivity.this, TabActivity.class);
                    intent.putExtra("Statement", statement);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(FilterActivity.this, "Alle Kriterien eingeben", Toast.LENGTH_SHORT);

                }

            }
        });




    }
    class CustomAdapter extends BaseAdapter
    {
        private LinkedList<View> list;
        private View view;
        private String krit;
        private LinkedList<String> filter;

        public CustomAdapter(LinkedList<String> filter)
        {
            this.filter = filter;
            list = new LinkedList<>();
        }

        public void addView(View v)
        {
            list.add(v);
        }

        public String getKrits(int pos)
        {


           krit =  ""+(((EditText) list.get(pos).findViewById(R.id.editText)).getText());

            return krit;
        }

        @Override
        public int getCount() {
            return filter.size();
        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if(view == null)
            view = getLayoutInflater().inflate(R.layout.customlayout, null);
            this.view = view;
            TextView tview = (TextView)this.view.findViewById(R.id.textView);
            EditText edtext = (EditText)this.view.findViewById(R.id.editText);

            tview.setText(filter.get(position));

            list.add(this.view);
            return this.view;
        }
    }

    private class CustomLayout extends RelativeLayout
    {
        private View rootview;
        private TextView textview;
        private EditText edittext;


        public CustomLayout(Context context) {
            super(context);

            rootview = inflate(context, R.layout.customlayout, this);
            textview = rootview.findViewById(R.id.textView);
            edittext = rootview.findViewById(R.id.editText);

        }
        public void setFilter(String text)
        {
            textview.setText(text);
        }

        public String getKrit()
        {
            return ""+(edittext.getText());
        }


    }


    private class CustomView extends View
    {


        public CustomView(Context context, String text) {
            super(context);

            TextView tv = new TextView(context);
            tv.setText(text);

          //  (TextView)((findViewById(R.id.customlayout)).findViewById(R.id.ListView)) = tv;

        }



    }
}
