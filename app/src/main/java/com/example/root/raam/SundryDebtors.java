package com.example.root.raam;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SundryDebtors extends BaseActivity
{
    DatabaseHelper db;
    ArrayList<DATA_ITEM> data=new ArrayList<>();
    CustomListAdapter adapter;
    View footer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sundry_debtors);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Sundry_Debtors));
        showFAB();
        String[] acc_features = getResources().getStringArray(R.array.Acc_Features);
        db=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);

        ListView list = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, data,"Debtor");
        list.setAdapter(adapter);
        footer=getLayoutInflater().inflate(R.layout.footer,null);
        list.addFooterView(footer);

    }

    private void getData()
    {
        int total=0,dtotal=0,ctotal=0;
        Cursor c=db.sortByName();
        if(c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            data.add(new DATA_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
            total+=Integer.parseInt(c.getString(4));
            dtotal+=Integer.parseInt(c.getString(2));
            ctotal+=Integer.parseInt(c.getString(3));
        }
        ((TextView)footer.findViewById(R.id.total_tv)).setText(Integer.toString(total));
        ((TextView)footer.findViewById(R.id.ctotal_tv)).setText(Integer.toString(ctotal));
        ((TextView)footer.findViewById(R.id.dtotal_tv)).setText(Integer.toString(dtotal));
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra(getString(R.string.ACC_TYPE),5));
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