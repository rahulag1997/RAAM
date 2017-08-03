package com.example.root.raam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class NewReceipt extends BaseActivity
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String[] acc_view_features;
    DatabaseHelper db_acc_debtor,db_acc_bank;
    int boundary;
    ArrayList<String> names=new ArrayList<>();
    EditText amtET;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_new_receipt);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Receipt));

        amtET=(EditText)findViewById(R.id.amountEditText);

        showFAB();
        String[] acc_features = getResources().getStringArray(R.array.Acc_Features);
        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);

        db_acc_debtor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
        getData(db_acc_debtor);
        boundary=names.size();
        db_acc_bank=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Bank), acc_features.length, acc_features);
        getData(db_acc_bank);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        final AutoCompleteTextView actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actv.setError(null);
                amtET.requestFocus();
            }
        });
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

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }
    public void setDate(View view)
    {
        // Get Current Date
        final TextView dateText=(TextView) findViewById(R.id.dateEditText);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
                    {
                        String day=Integer.toString(dayOfMonth);
                        String month=Integer.toString(monthOfYear+1);
                        String yearS=Integer.toString(year);
                        if(dayOfMonth<10)
                            day="0"+day;
                        if(monthOfYear<9)
                            month="0"+month;
                        if(year<10)
                            yearS="200"+yearS;
                        if(year<100)
                            yearS="20"+yearS;

                        dateText.setText(day + "-" + month + "-" + yearS);
                        dateText.setError(null);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public void submit(View view)
    {
        TextView dateET=(TextView) findViewById(R.id.dateEditText);
        EditText nameET=(EditText)findViewById(R.id.name_actv);
        final String date=(dateET.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        final String extraNote=((EditText)findViewById(R.id.notesEditText)).getText().toString();
        boolean showDialog=true;
        if(date.equals("")) {
            dateET.setError(getString(R.string.Required));
            showDialog = false;
        }
        if(!(names.contains(name)))
        {
            nameET.setError("Not in data");
            showDialog = false;
        }
        if(amount.equals(""))
        {
            amtET.setError(getString(R.string.Required));
            showDialog = false;
        }
        View customDialogView= View.inflate(this,R.layout.confirm_dialog,null);
        final CheckBox cb=(CheckBox)customDialogView.findViewById(R.id.cb);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm?");
        builder.setView(customDialogView);


        builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(cb.isChecked())
                {
                    Toast.makeText(getApplicationContext(),R.string.Confirmation_Disabled,Toast.LENGTH_SHORT).show();
                    editor=sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.SHOW_DIALOG),false);
                    editor.apply();

                }
                addNewReceipt(date,name,amount,extraNote);
            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(showDialog)
        {
            if(sharedPreferences.getBoolean(getString(R.string.SHOW_DIALOG),true))
                builder.create().show();
            else {
                addNewReceipt(date,name,amount,extraNote);
            }

        }
    }

    private void addNewReceipt(String date,String name,String amount,String extraNote)
    {
        int RPT_NUM=sharedPreferences.getInt(getString(R.string.RPT_NUM),1);
        String[] receipt_statement={"Receipt no "+RPT_NUM,amount,extraNote,getString(R.string.Receipt),date,""+RPT_NUM};
        boolean isBank=names.indexOf(name)>boundary-1;
        DatabaseHelper db;

        Cursor c_acc;
        if(isBank)
        {
            db=new DatabaseHelper(this,getString(R.string.Bank)+"_"+name,acc_view_features.length,acc_view_features);
            c_acc = db_acc_bank.getData();
        }
        else
        {
            db=new DatabaseHelper(this,getString(R.string.Debtor)+"_"+name,acc_view_features.length,acc_view_features);
            c_acc=db_acc_debtor.getData();
        }
        if(c_acc.getCount()!=0)
        {
            while (c_acc.moveToNext())
            {
                if(name.equals(c_acc.getString(1)))
                    break;
            }
        }
        int debit=Integer.parseInt(c_acc.getString(2));
        String three=c_acc.getString(3);
        int balance=Integer.parseInt(c_acc.getString(4));
        String five=c_acc.getString(5);
        //update acc list
        balance=balance-Integer.parseInt(amount);
        debit=debit+Integer.parseInt(amount);
        updateACC(name,Integer.toString(debit),three,Integer.toString(balance),five,isBank);

        //insert into party account
        db.insertData(receipt_statement);


        //insert into cash account
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.insertData(receipt_statement);

        //insert into receipts
        DatabaseHelper db_rpt=new DatabaseHelper(this,"Receipts",acc_view_features.length,acc_view_features);
        db_rpt.insertData(receipt_statement);

        //update cash in hand
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)+Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.CASH_IN_HAND),updatedCash);
        editor.putInt(getString(R.string.RPT_NUM),RPT_NUM+1);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Receipt Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewReceipt.class));
        finish();
    }
    private void updateACC(String name, String debit,String credit,String balance,String type,boolean isBank)
    {
        if(isBank)
            db_acc_bank.updateData(new String[] {name,debit,credit,balance,type});
        else
            db_acc_debtor.updateData(new String[] {name,debit,credit,balance,type});
    }
}

