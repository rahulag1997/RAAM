package com.example.root.raam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Expense));
        showFAB();
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
        EditText amtET=(EditText)findViewById(R.id.amountEditText);
        String details=((EditText)findViewById(R.id.detailsEditText)).getText().toString();
        if(details.equals(""))
            details="Expense";
        final String date=(dateET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        boolean showDialog=true;
        if(date.equals("")) {
            dateET.setError(getString(R.string.Required));
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
        String[] acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);

        //insert in cash account
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.insertData(new String[] {details+" "+date,amount,details,"Expense",date});

        //update cash balance
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)-Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt("CASH IN HAND",updatedCash);
        editor.apply();

        //insert in expense account
        DatabaseHelper db_exp=new DatabaseHelper(this,getString(R.string.Expense)+"_"+getString(R.string.EXP),acc_view_features.length,acc_view_features);
        db_exp.insertData(new String[] {details,amount,details,"Expense",date});

        //update expense balance
        int updatedExp=sharedPreferences.getInt(getString(R.string.EXP),0)+Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.EXP),updatedExp);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Expense Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewExpense.class));
        finish();
    }
}
