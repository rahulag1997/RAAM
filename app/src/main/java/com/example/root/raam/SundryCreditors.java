package com.example.root.raam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class SundryCreditors extends BaseActivity
{
    private final boolean hasFAB = true;
    ArrayList<DATA_ITEM> data=new ArrayList<DATA_ITEM>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sundry_creditors);
        getSupportActionBar().setTitle("Sundry Creditors");
        showFAB();
        fillData();
        ListView list = (ListView) findViewById(R.id.list);
        CustomListAdapter adpater = new CustomListAdapter(this, data);
        list.setAdapter(adpater);
    }

    private void fillData()
    {
        data.add(new DATA_ITEM("Ashok","0","0","100"));
        data.add(new DATA_ITEM("Aman","0","0","200"));
        data.add(new DATA_ITEM("Naresh","0","0","50"));
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
                    startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra("TYPE",4));
                }
            });
        }
        else
        {
            fab.hide();
        }
    }
}