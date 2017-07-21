package com.example.root.raam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BillView extends BaseActivity
{
    ArrayList<BILL_ITEM> data=new ArrayList<>();
    //CustomListAdapterBillItem c_adapter;
    Integer total=0;
    TextView total_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(R.string.Bill_No);
        showFab();

        View name_line=findViewById(R.id.name_line);
        ((TextView)name_line.findViewById(R.id.type_tv)).setText(getString(R.string.Name));
        ((TextView)name_line.findViewById(R.id.value_tv)).setText(R.string.Name_Party);

        View date_line=findViewById(R.id.date_line);
        ((TextView)date_line.findViewById(R.id.type_tv)).setText(getString(R.string.Date));
        ((TextView)date_line.findViewById(R.id.value_tv)).setText(getString(R.string.Date));

        ListView list=(ListView)findViewById(R.id.item_list);
       // c_adapter= new CustomListAdapterBillItem(this,data);
        //list.setAdapter(c_adapter);
        total_tv=(TextView)findViewById(R.id.total_tv);
        total_tv.setText(Integer.toString(total));


    }

    private void showFab()
    {
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data=new Bundle();
                data.putInt("TYPE",1);
                startActivity(new Intent(getApplicationContext(),NewBill.class).putExtra("DATA",data));
            }
        });
    }
}
