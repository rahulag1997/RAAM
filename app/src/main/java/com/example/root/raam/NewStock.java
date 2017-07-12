package com.example.root.raam;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NewStock extends BaseActivity
{
    private final boolean hasFAB = false;
    public final String[] type={"Stock","Stock Group"};
    String[] stck_grp={"Socks","Belt","Hat","Cap","Leggins"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        getSupportActionBar().setTitle("New Stock");
        showFAB();
        Spinner grp_spinner=(Spinner)findViewById(R.id.stock_group_spinner);
        ArrayAdapter<String> grp_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, stck_grp);
        grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grp_spinner.setAdapter(grp_adapter);
        final Spinner spinner = (Spinner) findViewById(R.id.stock_type_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(getIntent().hasExtra("TYPE"))
        {
            spinner.setSelection(getIntent().getIntExtra("TYPE",0));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==1)
                {
                    findViewById(R.id.quantity_ll).setVisibility(View.GONE);
                    findViewById(R.id.stock_grp_ll).setVisibility(View.GONE);
                }
                else
                {
                    findViewById(R.id.quantity_ll).setVisibility(View.VISIBLE);
                    findViewById(R.id.stock_grp_ll).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                spinner.setSelection(0);
            }
        });
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(hasFAB)
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //add command

                }
            });
        }
        else
        {
            fab.hide();
        }
    }

    public void submit(View view) {
    }
}
