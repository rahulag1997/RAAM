package com.example.root.EAS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PaymentView extends BaseActivity
{
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean editMode=true;
    private int boundary, boundary2, PMT_NUM,prev_amt;
    private String prev_name;
    private DatabaseHelper db_pmt,db_acc_creditor,db_acc_debtor,db_acc_bank;
    private String[] acc_view_features;
    private ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_view);

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        PMT_NUM=Integer.parseInt(getIntent().getStringExtra("PMT_NUM"));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Payment No. "+PMT_NUM);

        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        db_pmt=new DatabaseHelper(this,"Payments",acc_view_features.length,acc_view_features);
        Cursor c=db_pmt.getRowByNumber(PMT_NUM,"Payment");

        ((TextView)findViewById(R.id.date_line).findViewById(R.id.type_tv)).setText(getString(R.string.Date));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.type_tv)).setText(getString(R.string.Name));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.type_tv)).setText(getString(R.string.Amount));
        ((TextView)findViewById(R.id.notes_line).findViewById(R.id.type_tv)).setText(getString(R.string.Additional_Notes));
        c.moveToNext();
        prev_name=c.getString(1);
        prev_amt=Integer.parseInt(c.getString(2));
        ((TextView)findViewById(R.id.date_line).findViewById(R.id.value_tv)).setText(c.getString(5));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.value_tv)).setText(c.getString(1));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.value_tv)).setText(c.getString(2));
        ((TextView)findViewById(R.id.notes_line).findViewById(R.id.value_tv)).setText(c.getString(3));

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

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_black_24dp);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editMode)
                {
                    startActivity(new Intent(getApplicationContext(),NewPayment.class).putExtra("PMT_NUM",PMT_NUM));
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
    }

    private  void del()
    {
        init();
        editPayment();

    }

    private  void init()
    {
        names=new ArrayList<>();
        String[] acc_features = getResources().getStringArray(R.array.Acc_Features);
        db_pmt=new DatabaseHelper(this,"Payments",acc_view_features.length,acc_view_features);
        db_acc_creditor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Creditor), acc_features.length, acc_features);
        getData(db_acc_creditor);
        boundary=names.size();
        db_acc_bank=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Bank), acc_features.length, acc_features);
        getData(db_acc_bank);
        boundary2=names.size();
        db_acc_debtor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
        getData(db_acc_debtor);

    }

    private void getData(DatabaseHelper db)
    {
        Cursor c_acc=db.getData();
        if(c_acc.getCount()==0)
            return;
        while (c_acc.moveToNext())
        {
            names.add(c_acc.getString(1));
        }
    }

    private void editPayment()
    {
        boolean isBank=false,isCreditor=false;
        if(names.indexOf(prev_name)<boundary)
        {
            isCreditor=true;
            isBank=false;
        }
        else if(names.indexOf(prev_name)<boundary2)
        {
            isCreditor=false;
            isBank=true;
        }
        DatabaseHelper db;
        if(isBank)
        {
            Cursor c_acc=db_acc_bank.getDataByName(prev_name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();
                int credit=Integer.parseInt(c_acc.getString(3));
                int balance=Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance=balance-prev_amt;
                credit+=credit-prev_amt;
                db_acc_bank.updateData(new String[] {prev_name,c_acc.getString(2),Integer.toString(credit),Integer.toString(balance),c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Bank)+"_"+prev_name,acc_view_features.length,acc_view_features);
        }
        else if(isCreditor)
        {
            Cursor c_acc=db_acc_creditor.getDataByName(prev_name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();

                int debit = Integer.parseInt(c_acc.getString(2));
                int balance = Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance = balance +prev_amt;
                debit = debit -prev_amt;
                db_acc_creditor.updateData(new String[]{prev_name, Integer.toString(debit), c_acc.getString(3), Integer.toString(balance), c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Creditor)+"_"+prev_name,acc_view_features.length,acc_view_features);
        }
        else
        {
            Cursor c_acc=db_acc_debtor.getDataByName(prev_name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();
                int credit = Integer.parseInt(c_acc.getString(3));
                int balance = Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance = balance -prev_amt;
                credit = credit -prev_amt;
                db_acc_debtor.updateData(new String[]{prev_name, c_acc.getString(2), Integer.toString(credit), Integer.toString(balance), c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Debtor)+"_"+prev_name,acc_view_features.length,acc_view_features);
        }
        //delete from party account
        db.deleteVoucher(new String[] {getString(R.string.Payment),""+PMT_NUM});

        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        //update cash acc
        db_cashInHand.deleteVoucher(new String[] {getString(R.string.Payment),""+PMT_NUM});
        //update payment list
        db_pmt.deleteVoucher(new String[] {getString(R.string.Payment),""+PMT_NUM});
    }
}
