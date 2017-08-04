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

public class SundryCreditors extends BaseActivity
{
    private DatabaseHelper db;
    private CustomListAdapter adapter;
    private final ArrayList<DATA_ITEM> data=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sundry_creditors);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Sundry_Creditors));
        showFAB();
        String[] acc_features = this.getResources().getStringArray(R.array.Acc_Features);

        db=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Creditor), acc_features.length, acc_features);


        ListView list = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, data,getString(R.string.Creditor));
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
                startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra(getString(R.string.ACC_TYPE),4));
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