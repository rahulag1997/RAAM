package com.example.root.raam;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PurchaseView extends BaseActivity
{
    CustomListAdapterBillItemView c_adapter;
    ArrayList<BILL_ITEM> data;
    Integer total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);
        int PUR_NUM=Integer.parseInt(getIntent().getStringExtra("PUR_NUM"));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Purchase No. "+PUR_NUM);
        String[] acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        DatabaseHelper db=new DatabaseHelper(this,"Purchases",acc_view_features.length,acc_view_features);
        Cursor c=db.getRow(PUR_NUM);

        ((TextView)findViewById(R.id.date_line).findViewById(R.id.type_tv)).setText("Date");
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.type_tv)).setText("Name");

        c.moveToNext();
        ((TextView)findViewById(R.id.date_line).findViewById(R.id.value_tv)).setText(c.getString(5));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.value_tv)).setText(c.getString(1));

        data=new ArrayList<>();


        ListView list=(ListView)findViewById(R.id.item_list);
        c_adapter= new CustomListAdapterBillItemView(this,data);
        list.setAdapter(c_adapter);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_black_24dp);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Comming soon PUR",Toast.LENGTH_SHORT).show();

            }
        });
        getData(PUR_NUM);

    }

    private void getData(int BILLNO)
    {
        String[] item_fields=getResources().getStringArray(R.array.Item_Fields);
        DatabaseHelper db_bill=new DatabaseHelper(getApplicationContext(),"Purchase_"+BILLNO,item_fields.length,item_fields);
        Cursor c=db_bill.getData();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                data.add(new BILL_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5)));
            }
        }
        else
        {
            Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show();
        }
        c_adapter.notifyDataSetChanged();
    }
}
