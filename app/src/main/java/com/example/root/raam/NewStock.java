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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewStock extends BaseActivity
{
    public final String[] type={"Stock","Stock Group"};
    ArrayList<String> stock_grp_list;
    Spinner grp_spinner;
    Spinner type_spinner;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseHelper db;

    ArrayAdapter<String> grp_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        String[] sglf=this.getResources().getStringArray(R.array.StockGroupListFeatures);
        db=new DatabaseHelper(this,getString(R.string.SGL),sglf.length,sglf);

        stock_grp_list=new ArrayList<>();

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("New Stock");
        showFAB();

        grp_spinner=(Spinner)findViewById(R.id.stock_group_spinner);
        grp_adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, stock_grp_list);
        grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grp_spinner.setAdapter(grp_adapter);
        getStockGroup();

        type_spinner = (Spinner) findViewById(R.id.stock_type_spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, type);
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

    private void getStockGroup()
    {
        Cursor c=db.sortByName();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                stock_grp_list.add(c.getString(1));
            }
        }
        grp_adapter.notifyDataSetChanged();
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
                    name_et.setError(getString(R.string.Required));
                    return;
                }
                if(stock_grp_list.contains(name))
                {
                    name_et.setError(getString(R.string.Exists));
                    return;
                }
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
                        addNewStockGroup(name);
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
                if(sharedPreferences.getBoolean(getString(R.string.SHOW_DIALOG),true))
                    builder.create().show();
                else
                    addNewStockGroup(name);
                break;
            case 0:
                if(name.equals(""))
                {
                    name_et.setError(getString(R.string.Required));
                    hasError=true;
                }
                if(rate.equals(""))
                {
                    rate_et.setError(getString(R.string.Required));
                    hasError=true;
                }
                if(quantity.equals(""))
                {
                    quantity_et.setError(getString(R.string.Required));
                    hasError=true;
                }
                if(exists(name,grp_spinner.getSelectedItem().toString()))
                {
                    name_et.setError(getString(R.string.Exists));
                    hasError=true;
                }
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
                        addNewStock(grp_spinner.getSelectedItem().toString(),name,quantity,rate);
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
                if(!hasError)
                {
                    if(sharedPreferences.getBoolean(getString(R.string.SHOW_DIALOG),true))
                        builder.create().show();
                    else
                        addNewStock(grp_spinner.getSelectedItem().toString(),name,quantity,rate);
                }
                break;
        }
    }

    private boolean exists(String name, String s)
    {
        String[] sgf=this.getResources().getStringArray(R.array.StockGroup_Features);
        DatabaseHelper db=new DatabaseHelper(this,getString(R.string.SG)+"_"+s,sgf.length,sgf);
        Cursor c=db.getData();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
                if(name.equals(c.getString(1)))
                    return true;
        }
        return false;
    }

    private void addNewStockGroup(String name/*, String unit*/)
    {
        String[] fields=this.getResources().getStringArray(R.array.StockGroupListFeatures);
        DatabaseHelper db=new DatabaseHelper(this,getString(R.string.SGL),fields.length,fields);
        db.insertData(new String[] {name/*,unit*/});
        Toast.makeText(this,"Stock Group added successfully",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewStock.class).putExtra("TYPE",1));
        finish();

    }

    private void addNewStock(String stockGroup,String name, String quantity, String rate)
    {
        String[] features=this.getResources().getStringArray(R.array.Acc_Features);
        String[] fields=this.getResources().getStringArray(R.array.StockGroup_Features);

        //update into stockGroup
        DatabaseHelper db=new DatabaseHelper(this,getString(R.string.SG)+"_"+stockGroup,fields.length,fields);
        db.insertData(new String[] {name,quantity,rate});

        //create stock entry
        DatabaseHelper db_stock=new DatabaseHelper(this,stockGroup+"_"+name,features.length,features);
        db_stock.insertData(new String[] {getString(R.string.Opening_Balance),quantity,"",getString(R.string.OB),sharedPreferences.getString("Opening Day","01-01-2017")});


        Toast.makeText(this,"Stock added successfully",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewStock.class).putExtra("TYPE",0));
        finish();
    }


}
