package com.example.root.EAS;

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
    private String acc_type;
    private final ArrayList<DATA_ITEM> data=new ArrayList<>();
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        String name=getIntent().getStringExtra(getString(R.string.Name));
        acc_type=getIntent().getStringExtra(getString(R.string.ACC_TYPE));

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(name);

        showFAB();

        String[] acc_view_features = this.getResources().getStringArray(R.array.Acc_View_Features);
        db=new DatabaseHelper(this,acc_type+"_"+name, acc_view_features.length, acc_view_features);
        getData();

        TextView tv=(TextView)findViewById(R.id.data_name);
        tv.setText(R.string.Particulars);

        ListView list = (ListView) findViewById(R.id.list_particulars);
        CustomListAdapter2 adapter = new CustomListAdapter2(this, data);
        list.setAdapter(adapter);
    }

    private void getData()
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
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Payment" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Receipt":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this, R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Cash":
                    //FOR CASH
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Receipt" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Expense":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Payment":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Bill":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this,R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Expense":
                    //FOR EXPENSE
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Expense":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this,R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Sales":
                    //FOR SALES
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Bill":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this,R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Debtor":
                    //FOR DEBTOR
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Bill" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Receipt":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case  "Payment":
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this,R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Creditor":
                    //FOR CREDITOR
                    switch(rawData.getString(4))
                    {
                        case "OB" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---","---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Purchase" :
                            balance+=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),"---",rawData.getString(2),Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        case "Payment":
                            balance-=Integer.parseInt(rawData.getString(2));
                            data.add(new DATA_ITEM(rawData.getString(1),rawData.getString(2),"---",Integer.toString(balance),rawData.getString(4),rawData.getString(6)));
                            break;
                        default:Toast.makeText(this,R.string.Error_Feature_Mismatch,Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(this, R.string.Error_Account_Mismatch,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_delete_black_24dp);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO delete dialog and confirmation
            }
        });
    }
}
