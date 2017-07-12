package com.example.root.raam;

import android.content.DialogInterface;
import android.content.Intent;
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
    private final boolean hasFAB = true;
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
        if(hasFAB)
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivity(new Intent(getApplicationContext(),NewStock.class));
                }
            });
        }
        else
        {
            fab.hide();
        }
    }

    public void getData()
    {
        for (int j = 0; j < 5; j++)
        {
            Stock_group stock_group = new Stock_group(testStock[j]);
            for (int i = 0; i < 5; i++)
            {
                DATA_ITEM d=new DATA_ITEM(testStock[j]+i,"0","0",Integer.toString(i));
                stock_group.children.add(d);
            }
            stock_groups.append(j, stock_group);
        }
    }
}
