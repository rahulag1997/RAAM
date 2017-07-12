package com.example.root.raam;

import android.database.DataSetObserver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class NewAccount extends BaseActivity
{
    private final boolean hasFAB = false;
    private final String[] type={"Bank","Cash","Expense","Sales","Creditor","Debtor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        getSupportActionBar().setTitle("New Account");
        showFAB();
        Spinner spinner = (Spinner) findViewById(R.id.account_type_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(getIntent().hasExtra("TYPE"))
        {
            spinner.setSelection(getIntent().getIntExtra("TYPE",0));
        }
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

    public void submit(View view)
    {

    }
}
