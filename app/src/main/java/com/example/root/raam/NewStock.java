package com.example.root.raam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewStock extends BaseActivity
{
    public final String[] type={"Stock","Stock Group"};
    String[] stock_grp ={"Socks","Belt","Hat","Cap","Leggins"};
    Spinner grp_spinner;
    Spinner type_spinner;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("New Stock");
        showFAB();
        grp_spinner=(Spinner)findViewById(R.id.stock_group_spinner);
        ArrayAdapter<String> grp_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, stock_grp);
        grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grp_spinner.setAdapter(grp_adapter);
        type_spinner = (Spinner) findViewById(R.id.stock_type_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
        if(getIntent().hasExtra("TYPE"))
        {
            type_spinner.setSelection(getIntent().getIntExtra("TYPE",0));
        }
        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==1)
                {
                    findViewById(R.id.quantity_ll).setVisibility(View.GONE);
                    findViewById(R.id.stock_grp_ll).setVisibility(View.GONE);
                    findViewById(R.id.rate_ll).setVisibility(View.GONE);
                }
                else
                {
                    findViewById(R.id.quantity_ll).setVisibility(View.VISIBLE);
                    findViewById(R.id.stock_grp_ll).setVisibility(View.VISIBLE);
                    findViewById(R.id.rate_ll).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                type_spinner.setSelection(0);
            }
        });
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

        EditText name_et=(EditText)findViewById(R.id.name_et);
        final String name=name_et.getText().toString();
        EditText rate_et=(EditText)findViewById(R.id.rate_et);
        final String rate=rate_et.getText().toString();
        EditText quantity_et=(EditText)findViewById(R.id.quantity_et);
        final String quantity=quantity_et.getText().toString();
        boolean hasError=false;
        switch (type_spinner.getSelectedItemPosition())
        {
            case 1:
                if(name.equals("")) 
                {
                    name_et.setError("Required");
                    return;
                }
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
                        addNewStockGroup(name);
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
                if(sharedPreferences.getBoolean("SHOW_DIALOG",true))
                    builder.create().show();
                else
                    addNewStockGroup(name);
                break;
            case 0:
                if(name.equals(""))
                {
                    name_et.setError("Required");
                    hasError=true;
                }
                if(rate.equals(""))
                {
                    rate_et.setError("Required");
                    hasError=true;
                }
                if(quantity.equals(""))
                {
                    quantity_et.setError("Required");
                    hasError=true;
                }
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
                        addNewStock(name,quantity,rate);
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
                if(!hasError)
                {
                    if(sharedPreferences.getBoolean("SHOW_DIALOG",true))
                        builder.create().show();
                    else
                        addNewStock(name,quantity,rate);
                }
                break;
        }
    }

    private void addNewStockGroup(String name)
    {
        Toast.makeText(this,"Stock Group added successfully",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewStock.class).putExtra("TYPE",1));
        finish();

    }

    private void addNewStock(String name, String quantity, String rate)
    {
        Toast.makeText(this,"Stock added successfully",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewStock.class).putExtra("TYPE",0));
        finish();
    }


}
