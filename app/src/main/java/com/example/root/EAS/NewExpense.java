package com.example.root.EAS;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NewExpense extends BaseActivity
{
    int EXP_NUM;
    int prev_total;
    private boolean editMode;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView dateText;
    private EditText amtET,detailsET;
    DatabaseHelper db_exp;
    String[] acc_view_features;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Expense));
        showFAB();

        dateText=(TextView) findViewById(R.id.dateEditText);
        amtET=(EditText)findViewById(R.id.amountEditText);
        detailsET=(EditText)findViewById(R.id.detailsEditText);
        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        db_exp=new DatabaseHelper(this,getString(R.string.Expense)+"_"+getString(R.string.EXP),acc_view_features.length,acc_view_features);

        if(getIntent().hasExtra("EXP_NUM"))
        {
            editMode=true;
            EXP_NUM=getIntent().getIntExtra("EXP_NUM",1);
            setData();
        }
        else
        {
            editMode=false;
            EXP_NUM=sharedPreferences.getInt(getString(R.string.EXP_NUM),1);
        }

    }

    private void setData()
    {
        Cursor c=db_exp.getRowByNumber(EXP_NUM,"Expense");
        c.moveToNext();
        dateText.setText(c.getString(5));
        amtET.setText(c.getString(2));
        detailsET.setText(c.getString(1));
        prev_total=Integer.parseInt(c.getString(2));

    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    public void setDate(View view)
    {
        // Get Current Date
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
        String details=detailsET.getText().toString();
        if(details.equals(""))
            details="Expense";
        final String date=(dateText.getText()).toString();
        final String amount=(amtET.getText()).toString();
        boolean showDialog=true;
        if(date.equals("")) {
            dateText.setError(getString(R.string.Required));
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

        final String finalDetails = details;
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
                addNewExpense(date,amount, finalDetails);
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
                addNewExpense(date,amount,details);
            }

        }
    }

    private void addNewExpense(String date,String amount,String details)
    {
        if(editMode)
        {
            editExpense(date,amount,details);
            return;
        }

        String[] expense_statement={details,amount,details,"Expense",date,""+EXP_NUM};

        //insert in cash account
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.insertData(expense_statement);

        //update cash balance
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)-Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt("CASH IN HAND",updatedCash);
        editor.apply();

        //insert in expense account
        db_exp.insertData(expense_statement);

        //update expense balance
        int updatedExp=sharedPreferences.getInt(getString(R.string.EXP),0)+Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.EXP),updatedExp);
        editor.putInt(getString(R.string.EXP_NUM),EXP_NUM+1);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Expense Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewExpense.class));
        finish();
    }

    private void editExpense(String date, String amount, String details)
    {

        String[] expense_statement={details,amount,details,"Expense",date,""+EXP_NUM};

        //insert in cash account
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.updateVoucher(expense_statement);

        //update cash balance
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)+prev_total-Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt("CASH IN HAND",updatedCash);
        editor.apply();

        //insert in expense account
        db_exp.updateVoucher(expense_statement);

        //update expense balance
        int updatedExp=sharedPreferences.getInt(getString(R.string.EXP),0)-prev_total+Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.EXP),updatedExp);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Expense Edited",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewExpense.class));
        finish();
    }
}
