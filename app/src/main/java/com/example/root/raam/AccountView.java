package com.example.root.raam;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountView extends BaseActivity
{
    private String[] acc_view_features;
    ArrayList<DATA_ITEM> data=new ArrayList<>();
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        String name=getIntent().getStringExtra("Name");
        getSupportActionBar().setTitle(name);
        acc_view_features=this.getResources().getStringArray(R.array.acc_view_features);

        showFAB();

        TextView tv=(TextView)findViewById(R.id.data_name);
        tv.setText(R.string.particulars);

        db=new DatabaseHelper(this,name,acc_view_features.length,acc_view_features);
        getData();

        ListView list = (ListView) findViewById(R.id.list_particulars);
        CustomListAdapter2 adapter = new CustomListAdapter2(this, data);
        list.setAdapter(adapter);
    }

    public void getData()
    {
        Cursor rawData=db.getData();
        if(rawData.getCount()==0)
            return;
        while(rawData.moveToNext())
        {
            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),rawData.getString(3),rawData.getString(4)));
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }
}
