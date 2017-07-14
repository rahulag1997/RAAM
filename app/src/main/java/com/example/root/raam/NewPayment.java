package com.example.root.raam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseHelper db_acc_creditor,db_acc_bank;
    private String[] acc_view_features;
    public int boundary;


    ArrayList<String> names=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);
        getSupportActionBar().setTitle("Payment");
        showFAB();
        String[] acc_features = getResources().getStringArray(R.array.acc_features);
        acc_view_features=getResources().getStringArray(R.array.acc_view_features);


        db_acc_creditor=new DatabaseHelper(this,"Creditor", acc_features.length, acc_features);
        getData(db_acc_creditor);
        boundary=names.size();
        db_acc_bank=new DatabaseHelper(this,"Bank", acc_features.length, acc_features);
        getData(db_acc_bank);

        sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        AutoCompleteTextView actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        actv.setAdapter(adapter);
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
        TextView dateET=(TextView) findViewById(R.id.dateEditText);
        EditText nameET=(EditText)findViewById(R.id.name_actv);
        EditText amtET=(EditText)findViewById(R.id.amountEditText);
        final String date=(dateET.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        final String extraNote=((EditText)findViewById(R.id.notesEditText)).getText().toString();
        boolean showDialog=true;
        if(date.equals(""))
        {
            dateET.setError("Required");
            showDialog = false;
        }
        if(!(names.contains(name)))
        {
            nameET.setError("Not in data");
            showDialog = false;
        }
        if(amount.equals(""))
        {
            amtET.setError("Required");
            showDialog = false;
        }
        View customDialogView= View.inflate(this,R.layout.confirm_dialog,null);
        final CheckBox cb=(CheckBox)customDialogView.findViewById(R.id.cb);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm?");
        builder.setView(customDialogView);


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(cb.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"Confirmation Dialog Disabled",Toast.LENGTH_SHORT).show();
                    editor=sharedPreferences.edit();
                    editor.putBoolean("SHOW_DIALOG",false);
                    editor.apply();

                }
                addNewPayment(date,name,amount,extraNote);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(showDialog)
        {
            if(sharedPreferences.getBoolean("SHOW_DIALOG",true))
                builder.create().show();
            else
                addNewPayment(date,name,amount,extraNote);
        }
    }

    private void addNewPayment(String date,String name,String amount,String extraNote)
    {
        boolean isBank=names.indexOf(name)>boundary-1;
        DatabaseHelper db=new DatabaseHelper(this,name,acc_view_features.length,acc_view_features);

        Cursor c_acc;
        if(isBank)
            c_acc=db_acc_bank.getData();
        else
            c_acc=db_acc_creditor.getData();
        while (c_acc.moveToNext())
        {
            if(c_acc.getString(1).equals(name))
                break;
        }
        int balance=Integer.parseInt(c_acc.getString(4));
        int debit=Integer.parseInt(c_acc.getString(2));

        balance=balance-Integer.parseInt(amount);
        debit=debit+Integer.parseInt(amount);

        String[] data_item={"Payment on "+date,amount,"---",Integer.toString(balance),extraNote,"Payment"};

        db.insertData(data_item);

        updateACC(name,Integer.toString(debit),c_acc.getString(3),Integer.toString(balance),c_acc.getString(5),isBank);

        int updatedCash=sharedPreferences.getInt("CASH_IN_HAND",0)-Integer.parseInt(amount);

        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.cash_in_hand),acc_view_features.length,acc_view_features);
        db_cashInHand.insertData(new String[] {name+" "+date,amount,"---",Integer.toString(updatedCash),extraNote,"Payment"});
        editor=sharedPreferences.edit();
        editor.putInt("CASH_IN_HAND",updatedCash);
        editor.apply();


        Toast.makeText(getApplicationContext(),"Payment Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewPayment.class));
        finish();
    }

    private void updateACC(String name, String debit,String credit,String balance,String type,boolean isBank)
    {
        if(isBank)
            db_acc_bank.updateData(new String[] {name,debit,credit,balance,type});
        else
            db_acc_creditor.updateData(new String[] {name,debit,credit,balance,type});
    }
}
