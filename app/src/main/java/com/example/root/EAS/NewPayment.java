package com.example.root.EAS;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
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

public class NewPayment extends BaseActivity
{
    private boolean editMode;
    private int prev_amt;
    private String prev_name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseHelper db_acc_creditor, db_acc_bank, db_acc_debtor, db_pmt;
    private String[] acc_view_features;
    private int boundary, boundary2, PMT_NUM;
    private EditText amtET;
    private TextView dateText;
    AutoCompleteTextView ac_tv;

    private ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);
        names=new ArrayList<>();

        init();

        if(getIntent().hasExtra("PMT_NUM"))
        {
            editMode=true;
            PMT_NUM=getIntent().getIntExtra("PMT_NUM",1);
            setData();
        }
        else
        {
            editMode=false;
            PMT_NUM=sharedPreferences.getInt("PMT_NUM",1);
        }

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Payment No."+PMT_NUM);
        showFAB();
    }

    private void setData()
    {
        Cursor c=db_pmt.getRowByNumber(PMT_NUM,"Payment");
        c.moveToNext();
        dateText.setText(c.getString(5));
        ac_tv.setText(c.getString(1));
        amtET.setText(c.getString(2));
        prev_amt=Integer.parseInt(c.getString(2));
        prev_name=c.getString(1);
    }

    private void init()
    {
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        amtET=(EditText)findViewById(R.id.amountEditText);
        dateText=(TextView) findViewById(R.id.dateEditText);

        String[] acc_features = getResources().getStringArray(R.array.Acc_Features);
        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);

        db_pmt=new DatabaseHelper(this,"Payments",acc_view_features.length,acc_view_features);
        db_acc_creditor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Creditor), acc_features.length, acc_features);
        getData(db_acc_creditor);
        boundary=names.size();
        db_acc_bank=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Bank), acc_features.length, acc_features);
        getData(db_acc_bank);
        boundary2=names.size();
        db_acc_debtor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
        getData(db_acc_debtor);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        ac_tv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        ac_tv.setAdapter(adapter);
        ac_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
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
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
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
        final String date=(dateText.getText()).toString();
        final String name=(ac_tv.getText()).toString();
        final String amount=(amtET.getText()).toString();
        final String extraNote=((EditText)findViewById(R.id.notesEditText)).getText().toString();
        boolean showDialog=true;
        if(date.equals(""))
        {
            dateText.setError(getString(R.string.Required));
            showDialog = false;
        }
        if(!(names.contains(name)))
        {
            ac_tv.setError("Not in data");
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
                    editor.putBoolean(getString(R.string.SHOW_DIALOG),false);
                    editor.apply();

                }
                addNewPayment(date,name,amount,extraNote);
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
            else
                addNewPayment(date,name,amount,extraNote);
        }
    }

    private void addNewPayment(String date,String name,String amount,String extraNote)
    {
        if(editMode)
        {
            editPayment();
        }
        boolean isBank=false,isCreditor=false;
        if(names.indexOf(name)<boundary)
        {
            isCreditor=true;
            isBank=false;
        }
        else if(names.indexOf(name)<boundary2)
        {
            isCreditor=false;
            isBank=true;
        }
        DatabaseHelper db;
        if(isBank)
        {
            Cursor c_acc=db_acc_bank.getDataByName(name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();
                int credit=Integer.parseInt(c_acc.getString(3));
                int balance=Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance=balance+Integer.parseInt(amount);
                credit+=credit+Integer.parseInt(amount);
                db_acc_bank.updateData(new String[] {name,c_acc.getString(2),Integer.toString(credit),Integer.toString(balance),c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Bank)+"_"+name,acc_view_features.length,acc_view_features);
        }
        else if(isCreditor)
        {
            Cursor c_acc=db_acc_creditor.getDataByName(name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();

                int debit = Integer.parseInt(c_acc.getString(2));
                int balance = Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance = balance - Integer.parseInt(amount);
                debit = debit + Integer.parseInt(amount);
                db_acc_creditor.updateData(new String[]{name, Integer.toString(debit), c_acc.getString(3), Integer.toString(balance), c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Creditor)+"_"+name,acc_view_features.length,acc_view_features);
        }
        else
        {
            Cursor c_acc=db_acc_debtor.getDataByName(name);
            if(c_acc.getCount()!=0)
            {
                c_acc.moveToNext();
                int credit = Integer.parseInt(c_acc.getString(3));
                int balance = Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance = balance + Integer.parseInt(amount);
                credit = credit + Integer.parseInt(amount);
                db_acc_debtor.updateData(new String[]{name, c_acc.getString(2), Integer.toString(credit), Integer.toString(balance), c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Debtor)+"_"+name,acc_view_features.length,acc_view_features);
        }
        String[] payment_statement={name,amount,extraNote,getString(R.string.Payment),date,""+PMT_NUM};

        //insert in party account
        db.insertData(payment_statement);

        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);

        if(editMode)
        {
            //update cash acc
            db_cashInHand.updateVoucher(payment_statement);
            //update payment list
            db_pmt.updateVoucher(payment_statement);
        }
        else
        {
            //insert into cash acc
            db_cashInHand.insertData(payment_statement);
            //insert into payment list
            db_pmt.insertData(payment_statement);
        }
        String Message;
        //update cash
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)+prev_amt-Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.CASH_IN_HAND),updatedCash);
        if(editMode)
        {
            Message="Payment Edited";
        }
        else
        {
            Message="Payment Added";
            editor.putInt(getString(R.string.PMT_NUM),PMT_NUM+1);
        }
        editor.apply();


        Toast.makeText(getApplicationContext(), Message,Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewPayment.class));
        finish();
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
    }

}
