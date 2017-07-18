package com.example.root.raam;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AccountView extends BaseActivity
{
    private String[] acc_view_features;
    private String acc_type;
    ArrayList<DATA_ITEM> data=new ArrayList<>();
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        String name=getIntent().getStringExtra("Name");
        acc_type=getIntent().getStringExtra("ACC_TYPE");
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
        int balance=0;
        while(rawData.moveToNext())
        {
            switch(acc_type)
            {
                case "Bank":
                    //FOR BANK
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Payment" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        case "Receipt":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Cash":
                    //FOR CASH
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Receipt" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        case "Expense":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance)));
                            break;
                        case "Payment":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Expense":
                    //FOR EXPENSE
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Expense":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Sales":
                    //FOR SALES
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Bill":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Debtor":
                    //FOR DEBTOR
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Bill" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        case "Receipt":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Creditor":
                    //FOR CREDITOR
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance)));
                            break;
                        case "Purchase" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance)));
                            break;
                        case "Payment":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance)));
                            break;
                        default:Toast.makeText(this,"Error in matching the account feature",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(this,"Error in matching acc type",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }
}
