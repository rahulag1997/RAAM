package com.example.root.raam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class StockView extends BaseActivity
{
    private final boolean hasFAB = false;
    ArrayList<DATA_ITEM> data=new ArrayList<DATA_ITEM>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        String name=getIntent().getStringExtra("Name");
        getSupportActionBar().setTitle(name);
        showFAB();
        ((TextView)findViewById(R.id.data_name)).setText(R.string.Particulars);
        ((TextView)findViewById(R.id.data_dr)).setText(R.string.Sale);
        ((TextView)findViewById(R.id.data_cr)).setText(R.string.Purchase);

        fillData();
        ListView list = (ListView) findViewById(R.id.list_particulars);
        CustomListAdapter2 adapter = new CustomListAdapter2(this, data);
        list.setAdapter(adapter);
    }

    private void fillData()
    {
        data.add(new DATA_ITEM("Opening Balance","--","--","500"));
        data.add(new DATA_ITEM("Bill No. 5","100","--","400"));
        data.add(new DATA_ITEM("Purchase No. 2","--","200","600"));
        data.add(new DATA_ITEM("Bill No. 3","600","--","400"));
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

                    startActivity(new Intent(getApplicationContext(),NewAccount.class));

                }
            });
        }
        else
        {
            fab.hide();
        }
    }
}
