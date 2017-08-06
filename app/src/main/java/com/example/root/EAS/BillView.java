package com.example.root.EAS;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class BillView extends BaseActivity
{
    private final DecimalFormat dec_format=new DecimalFormat("#");
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean editMode=true;
    private CustomListAdapterBillItemView c_adapter;
    private ArrayList<BILL_ITEM> data;
    private FloatingActionButton fab;
    private String[] acc_view_features,acc_features,sgf;
    private int BILL_NUM;
    private String prev_name;
    private int prev_total;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        BILL_NUM=Integer.parseInt(getIntent().getStringExtra("BILL_NUM"));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Bill No. "+BILL_NUM);

        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        DatabaseHelper db=new DatabaseHelper(this,"Bills",acc_view_features.length,acc_view_features);
        Cursor c=db.getRowByNumber(BILL_NUM,"Bill");

        ((TextView)findViewById(R.id.date_line).findViewById(R.id.type_tv)).setText(getString(R.string.Date));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.type_tv)).setText(getString(R.string.Name));

        c.moveToNext();
        prev_name=c.getString(1);
        prev_total=Integer.parseInt(c.getString(2));
        ((TextView)findViewById(R.id.date_line).findViewById(R.id.value_tv)).setText(c.getString(5));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.value_tv)).setText(c.getString(1));

        data=new ArrayList<>();


        ListView list=(ListView)findViewById(R.id.item_list);
        c_adapter= new CustomListAdapterBillItemView(this,data);
        list.setAdapter(c_adapter);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_black_24dp);

        View customDialogView= View.inflate(getApplicationContext(),R.layout.confirm_dialog,null);
        final CheckBox cb=(CheckBox)customDialogView.findViewById(R.id.cb);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm?");
        builder.setView(customDialogView);
        builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(cb.isChecked())
                {
                    Toast.makeText(getApplicationContext(),R.string.Confirmation_Disabled,Toast.LENGTH_SHORT).show();
                    editor=sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.CONFIRM_DEL),false);
                    editor.apply();
                }
                del();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editMode)
                {
                    startActivity(new Intent(getApplicationContext(), NewBill.class).putExtra("BILL_NUM", BILL_NUM));
                    finish();
                }
                else
                {
                    if(sharedPreferences.getBoolean(getString(R.string.CONFIRM_DEL),true))
                        builder.create().show();
                    else
                    {
                        del();
                        finish();
                    }

                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(editMode)
                    fab.setImageResource(R.drawable.ic_delete_black_24dp);
                else
                    fab.setImageResource(R.drawable.ic_edit_black_24dp);
                editMode=!editMode;
                return true;
            }
        });
        getData(BILL_NUM);

    }
    private void del()
    {
        init();
        editBill();
        new ResetStock().execute();
    }

    private void init()
    {
        acc_features = getResources().getStringArray(R.array.Acc_Features);
        sgf=getResources().getStringArray(R.array.StockGroup_Features);
    }

    private void getData(int BILL_NO)
    {
        String[] item_fields=getResources().getStringArray(R.array.Item_Fields);
        DatabaseHelper db_bill=new DatabaseHelper(getApplicationContext(),"Bill_"+BILL_NO,item_fields.length,item_fields);
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

    private void editBill()
    {
        DatabaseHelper db_party;
        if (prev_name.equals("Cash"))
        {
            //update cash balance
            int updatedCash = sharedPreferences.getInt(getString(R.string.CASH_IN_HAND), 0) - prev_total;
            editor = sharedPreferences.edit();
            editor.putInt("CASH IN HAND", updatedCash);
            editor.apply();

            //Delete from cash account
            db_party = new DatabaseHelper(this, getString(R.string.Cash) + "_" + getString(R.string.Cash_in_Hand), acc_view_features.length, acc_view_features);
            db_party.deleteVoucher(new String[]{getString(R.string.Bill), dec_format.format(BILL_NUM)});

            //Delete from sales account
            DatabaseHelper db_sales = new DatabaseHelper(this, getString(R.string.Sales) + "_" + getString(R.string.Cash), acc_view_features.length, acc_view_features);
            db_sales.deleteVoucher(new String[]{getString(R.string.Bill), dec_format.format(BILL_NUM)});

            //update sales balance
            editor = sharedPreferences.edit();
            int updatedSales = sharedPreferences.getInt(getString(R.string.SALES_CASH), 0) - prev_total;
            editor.putInt(getString(R.string.SALES_CASH), updatedSales);
            editor.apply();
        }
        else
        {
            DatabaseHelper db_accList =new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
            //Update party balance in account list
            Cursor cursor_accList = db_accList.getDataByName(prev_name);
            cursor_accList.moveToNext();
            int balance = Integer.parseInt(cursor_accList.getString(4));
            int credit = Integer.parseInt(cursor_accList.getString(3));

            balance = balance - prev_total;
            credit = credit - prev_total;

            db_accList.updateData(new String[]{prev_name, cursor_accList.getString(2), dec_format.format(credit), dec_format.format(balance), cursor_accList.getString(5)});

            //Delete from prev acc
            DatabaseHelper db_party_org = new DatabaseHelper(this, getString(R.string.Debtor) + "_" + prev_name, acc_view_features.length, acc_view_features);
            db_party_org.deleteVoucher(new String[]{getString(R.string.Bill), dec_format.format(BILL_NUM)});

            //update in sales account
            DatabaseHelper db_sales = new DatabaseHelper(this, getString(R.string.Sales) + "_" + getString(R.string.Credit), acc_view_features.length, acc_view_features);
            db_sales.deleteVoucher(new String[]{getString(R.string.Bill), dec_format.format(BILL_NUM)});

            //update sales balance
            editor = sharedPreferences.edit();
            int updatedSales = sharedPreferences.getInt(getString(R.string.SALES_CREDIT), 0) - prev_total;
            editor.putInt(getString(R.string.SALES_CREDIT), updatedSales);
            editor.apply();
        }

        DatabaseHelper db_bills=new DatabaseHelper(this,getString(R.string.Bills),acc_view_features.length,acc_view_features);
        db_bills.deleteVoucher(new String[]{getString(R.string.Bill),dec_format.format(BILL_NUM)});
    }
    private class ResetStock extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            for (BILL_ITEM item:data)
            {
                //update stock list
                DatabaseHelper db_sg=new DatabaseHelper(getApplicationContext(),getString(R.string.SG)+"_"+item.stk_grp,sgf.length,sgf);
                Cursor c=db_sg.getDataByName(item.stk_item);
                if(c.getCount()!=0)
                {
                    c.moveToNext();
                    int sold=Integer.parseInt(c.getString(2))-Integer.parseInt(item.quantity);
                    int bal=Integer.parseInt(c.getString(4))+Integer.parseInt(item.quantity);
                    db_sg.updateData(new String[]{item.stk_item,dec_format.format(sold),c.getString(3),dec_format.format(bal),item.rate});
                }
                //insert into stock data
                //acc_features is the features that a stocks in a stock group will have
                DatabaseHelper db_stock=new DatabaseHelper(getApplicationContext(),item.stk_grp+"_"+item.stk_item,acc_features.length,acc_features);
                db_stock.deleteRowByBillName("Bill_"+ BILL_NUM);
            }
            return null;
        }
    }
}
