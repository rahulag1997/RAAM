package com.example.root.raam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewReceipt extends BaseActivity
{
    SharedPreferences sharedPreferences;
    List<String> names= Arrays.asList("Rahul","Tom","Ashok","Sid","Raj","Aryan","Sidd","Ram","Rajesh","Ratan","Rashi");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_new_receipt);
        getSupportActionBar().setTitle("Receipt");

        showFAB();

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        final AutoCompleteTextView actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actv.setError(null);
            }
        });
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
        EditText amtET=(EditText)findViewById(R.id.amountEditText);
        final String date=(dateET.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        final String extraNote=((EditText)findViewById(R.id.notesEditText)).getText().toString();
        boolean showDialog=true;
        if(date.equals("")) {
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
        View customDialogView= (View)View.inflate(this,R.layout.confirm_dialog,null);
        final CheckBox cb=(CheckBox)customDialogView.findViewById(R.id.cb);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm?");
        builder.setView(customDialogView);


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(cb.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"Confirmation Dialog Disabled",Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("SHOW_DIALOG",false);
                    editor.apply();

                }
                addNewReceipt(date,name,amount,extraNote);
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
            else {
                addNewReceipt(date,name,amount,extraNote);
            }

        }
    }

    private void addNewReceipt(String date,String name,String amount,String extraNote)
    {
        Toast.makeText(getApplicationContext(),"Receipt Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewReceipt.class));
        finish();
    }
}

