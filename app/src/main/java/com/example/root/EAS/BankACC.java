package com.example.root.EAS;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BankACC extends BaseActivity
{
    private DatabaseHelper db;
    private ArrayList<DATA_ITEM> data;
    private CustomListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_acc);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Bank_Acc));

        showFAB();

        data=new ArrayList<>();
        String[] acc_features = this.getResources().getStringArray(R.array.Acc_Features);
        db=new DatabaseHelper(this,getString(R.string.Account_Bank), acc_features.length, acc_features);
        ListView list = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, data,getString(R.string.Bank));
        list.setAdapter(adapter);
    }

    private void getData()
    {
        int total=0,d_total=0,c_total=0;
        Cursor c=db.sortByName();
        if(c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            data.add(new DATA_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
            total+=Integer.parseInt(c.getString(4));
            d_total+=Integer.parseInt(c.getString(2));
            c_total+=Integer.parseInt(c.getString(3));
        }
        DecimalFormat dec_format=new DecimalFormat("#");
        ((TextView)findViewById(R.id.total_tv)).setText(dec_format.format(total));
        ((TextView)findViewById(R.id.ctotal_tv)).setText(dec_format.format(c_total));
        ((TextView)findViewById(R.id.dtotal_tv)).setText(dec_format.format(d_total));
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra(getString(R.string.ACC_TYPE),0));
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        data.clear();
        getData();
        adapter.notifyDataSetChanged();
    }
}
