package com.example.root.raam;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SundryCreditors extends BaseActivity
{
    private String[] acc_features;
    DatabaseHelper db;
    CustomListAdapter adapter;
    ArrayList<DATA_ITEM> data=new ArrayList<DATA_ITEM>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sundry_creditors);
        getSupportActionBar().setTitle("Sundry Creditors");
        showFAB();
        acc_features=this.getResources().getStringArray(R.array.acc_features);

        db=new DatabaseHelper(this,"Creditor",acc_features.length,acc_features);


        ListView list = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, data,"Creditor");
        list.setAdapter(adapter);
    }

    private void getData()
    {
        int total=0;
        Cursor c=db.sortByName();
        if(c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            data.add(new DATA_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
            total+=Integer.parseInt(c.getString(4));
        }
        ((TextView)findViewById(R.id.total_tv)).setText(Integer.toString(total));
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra("TYPE",4));
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