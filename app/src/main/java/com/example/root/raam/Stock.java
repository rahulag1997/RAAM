package com.example.root.raam;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class Stock extends BaseActivity
{
    String[] type={"Stock Group", "Stock"};
    String[] testStock={"Socks","Belt","Hat","Cap","Leggins"};
    SparseArray<Stock_group> stock_groups = new SparseArray<Stock_group>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        getSupportActionBar().setTitle("Stock");

        getData();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.stock_list);

        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(this,stock_groups);
        listView.setAdapter(adapter);
        showFAB();
    }


    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewStock.class));
            }
        });
    }

    public void getData()
    {
        int j=0;
        String[] sglf=this.getResources().getStringArray(R.array.StockGroupListFeatures);
        String[] sgf=this.getResources().getStringArray(R.array.StockGroup_Features);
        DatabaseHelper db_sgl=new DatabaseHelper(this,getString(R.string.SGL),sglf.length,sglf);
        Cursor c_sgl=db_sgl.sortByName();
        if(c_sgl.getCount()!=0)
        {
            while (c_sgl.moveToNext())
            {
                String SGName=c_sgl.getString(1);
                Stock_group stock_group=new Stock_group(SGName);
                DatabaseHelper db_sg=new DatabaseHelper(this,getString(R.string.SG)+"_"+SGName,sgf.length,sgf);
                Cursor c_sg=db_sg.sortByName();
                if(c_sg.getCount()!=0)
                {
                    while (c_sg.moveToNext())
                    {
                        stock_group.children.add(new DATA_ITEM(c_sg.getString(1),c_sg.getString(2),c_sg.getString(3),c_sg.getString(4)));
                    }
                }
                stock_groups.append(j++,stock_group);
            }
        }
    }
}
