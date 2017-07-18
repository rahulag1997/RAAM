package com.example.root.raam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewAccount extends BaseActivity
{
    private String[] acc_types;
    private String[] acc_features;
    private String[] acc_view_features;

    Spinner spinner;
    SharedPreferences sharedPreferences;
    EditText name_et,val_et;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        getSupportActionBar().setTitle("New Account");
        showFAB();

        acc_types=this.getResources().getStringArray(R.array.acc_types);
        acc_features=this.getResources().getStringArray(R.array.acc_features);
        acc_view_features=this.getResources().getStringArray(R.array.acc_view_features);

        //need for SHOW_DIALOG and SHOW_REPEAT
        sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        name_et=(EditText) findViewById(R.id.name_et);
        val_et=(EditText)findViewById(R.id.valEditText);

        spinner = (Spinner) findViewById(R.id.account_type_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, acc_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //For selecting default type
        if(getIntent().hasExtra("TYPE"))
        {
            spinner.setSelection(getIntent().getIntExtra("TYPE",0));
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    public void submit(View view)
    {
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
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("SHOW_DIALOG",false);
                    editor.apply();
                }
                addNewAccount();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        String name=name_et.getText().toString();

        if(name.equals(""))
            name_et.setError("Mandatory");
        else
        {
            db=new DatabaseHelper(this,spinner.getSelectedItem().toString(),acc_features.length, acc_features);
            Cursor cursor=db.getData();
            if(cursor.getCount()!=0)
            {
                while (cursor.moveToNext())
                {
                    if(cursor.getString(1).equals(name)) //1 is the coloumn corresponding to the name field
                    {
                        name_et.setError("Already Exists");
                        return;
                    }
                }
            }
            if(sharedPreferences.getBoolean("SHOW_DIALOG",true))
                builder.create().show();
            else
            {
                addNewAccount();
            }
        }
    }

    private void addNewAccount()
    {
        String amount=val_et.getText().toString();
        String name=name_et.getText().toString();

        if(amount.equals(""))
            amount="0";
        //add into list of accounts
        db.insertData(new String[] {name,"0","0",amount,spinner.getSelectedItem().toString()});

        //add opening balance into acc
        DatabaseHelper db_party=new DatabaseHelper(this,name,acc_view_features.length,acc_view_features);
        db_party.insertData(new String[]{getString(R.string.ob),amount,"","OB",sharedPreferences.getString("Opening Day","01-01-2017")});    //ob->opening balance

        Toast.makeText(getApplicationContext(),"Account Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewAccount.class).putExtra("TYPE",spinner.getSelectedItemPosition()));
        finish();
    }
}
