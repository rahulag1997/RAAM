package com.example.root.raam;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class Stock extends BaseActivity
{
    SparseArray<Stock_group> stock_groups;
    CustomExpandableListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        getSupportActionBar().setTitle("Stock");

        stock_groups = new SparseArray<>();

        View title=findViewById(R.id.title_line);
        ((TextView)title.findViewById(R.id.data_dr)).setText("Sold");
        ((TextView)title.findViewById(R.id.data_cr)).setText("Purchased");

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.stock_list);

        adapter = new CustomExpandableListAdapter(this,stock_groups);

        listView.setAdapter(adapter);

        getData();

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
        stock_groups.clear();
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
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }
}
