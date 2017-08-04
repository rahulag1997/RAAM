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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseHelper db_acc_creditor;
    private DatabaseHelper db_acc_bank;
    private DatabaseHelper db_acc_debtor;
    private String[] acc_view_features;
    private int boundary;
    private int boundary2;
    private EditText amtET;

    private final ArrayList<String> names=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(R.string.Payment);
        showFAB();
        amtET=(EditText)findViewById(R.id.amountEditText);

        String[] acc_features = getResources().getStringArray(R.array.Acc_Features);
        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);


        db_acc_creditor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Creditor), acc_features.length, acc_features);
        getData(db_acc_creditor);
        boundary=names.size();
        db_acc_bank=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Bank), acc_features.length, acc_features);
        getData(db_acc_bank);
        boundary2=names.size();
        db_acc_debtor=new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
        getData(db_acc_debtor);

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);


        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        AutoCompleteTextView ac_tv=(AutoCompleteTextView)findViewById(R.id.name_actv);
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
        TextView dateET=(TextView) findViewById(R.id.dateEditText);
        EditText nameET=(EditText)findViewById(R.id.name_actv);
        final String date=(dateET.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        final String extraNote=((EditText)findViewById(R.id.notesEditText)).getText().toString();
        boolean showDialog=true;
        if(date.equals(""))
        {
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
        boolean isBank=false,isCreditor=false;
        if(name.indexOf(name)<boundary)
        {
            isCreditor=true;
            isBank=false;
        }
        else if(name.indexOf(name)<boundary2)
        {
            isCreditor=false;
            isBank=true;
        }
        DatabaseHelper db;
        if(isBank)
        {
            Cursor c_acc=db_acc_bank.getData();
            if(c_acc.getCount()!=0)
            {
                while (c_acc.moveToNext())
                {
                    if (name.equals(c_acc.getString(1)))
                        break;
                }
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
            Cursor c_acc=db_acc_creditor.getData();
            if(c_acc.getCount()!=0)
            {
                while (c_acc.moveToNext()) {
                    if (name.equals(c_acc.getString(1)))
                        break;
                }

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
            Cursor c_acc=db_acc_debtor.getData();
            if(c_acc.getCount()!=0)
            {
                while (c_acc.moveToNext()) {
                    if (name.equals(c_acc.getString(1)))
                        break;
                }
                int credit = Integer.parseInt(c_acc.getString(3));
                int balance = Integer.parseInt(c_acc.getString(4));

                //update acc list
                balance = balance + Integer.parseInt(amount);
                credit = credit + Integer.parseInt(amount);
                db_acc_debtor.updateData(new String[]{name, c_acc.getString(2), Integer.toString(credit), Integer.toString(balance), c_acc.getString(5)});
            }
            db=new DatabaseHelper(this,getString(R.string.Debtor)+"_"+name,acc_view_features.length,acc_view_features);
        }
        int PMT_NUM=sharedPreferences.getInt(getString(R.string.PMT_NUM),1);
        String[] payment_statement={"Payment No "+PMT_NUM,amount,extraNote,getString(R.string.Payment),date,""+PMT_NUM};


        //insert in party account
        db.insertData(payment_statement);

        //insert into cash acc
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.insertData(payment_statement);

        //insert into payment list
        DatabaseHelper db_pmt=new DatabaseHelper(this,"Payments",acc_view_features.length,acc_view_features);
        db_pmt.insertData(payment_statement);

        //update cash
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)-Integer.parseInt(amount);
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.CASH_IN_HAND),updatedCash);
        editor.putInt(getString(R.string.PMT_NUM),PMT_NUM+1);
        editor.apply();


        Toast.makeText(getApplicationContext(),"Payment Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewPayment.class));
        finish();
    }

}
