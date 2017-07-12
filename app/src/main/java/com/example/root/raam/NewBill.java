package com.example.root.raam;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewBill extends BaseActivity
{
    SharedPreferences sharedPreferences;

    private int mYear, mMonth, mDay;
    List<String> names= Arrays.asList("Rahul","Aman","Ashok","Sid","Raj","Aryan","Sidd","Ram","Rajesh","Ratan","Rashi");
    String[] testStock={"Socks","Belt","Hat","Cap","Leggins"};
    String[] testStock_list={"Socks1","Belt2","Hat3","Cap4","Leggins5"};
    String[] units={"Dz","Pcs"};
    ArrayList<BILL_ITEM> data=new ArrayList<BILL_ITEM>();
    CustomListAdapterBillItem c_adapter;
    Integer total=0;
    TextView total_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);
        sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("Bill");
        showFAB();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,names);
        AutoCompleteTextView actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        actv.setAdapter(adapter);

        ListView list=(ListView)findViewById(R.id.item_list);
        c_adapter= new CustomListAdapterBillItem(this,data);
        list.setAdapter(c_adapter);
        total_tv=(TextView)findViewById(R.id.total_tv);
        total_tv.setText(Integer.toString(total));
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
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

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

    public void addItem(View view)
    {
        View new_item=View.inflate(this,R.layout.new_item,null);
        final Spinner stk_grp_spinner=(Spinner)new_item.findViewById(R.id.stock_group_spinner);
        ArrayAdapter<String> stk_grp_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,testStock);
        stk_grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stk_grp_spinner.setAdapter(stk_grp_adapter);

        final Spinner stk_item_spinner=(Spinner)new_item.findViewById(R.id.stock_spinner);
        final ArrayAdapter<String> stk_item_adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,testStock_list);
        stk_item_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stk_item_spinner.setAdapter(stk_item_adapter);

        stk_grp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                testStock_list[position]=testStock[position];
                stk_item_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        final Spinner unit_spinner=(Spinner)new_item.findViewById(R.id.unit_spinner);
        ArrayAdapter<String> unit_adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,units);
        unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit_spinner.setAdapter(unit_adapter);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("New Item");
        builder.setView(new_item);

        final EditText quantity_et=(EditText)new_item.findViewById(R.id.quantity_et);
        final EditText rate_et=(EditText)new_item.findViewById(R.id.rate_et);

        builder.setPositiveButton("Add",null);
        final AlertDialog a_dialog=builder.create();
        a_dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog) {

                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String quantity=quantity_et.getText().toString();
                        String rate=rate_et.getText().toString();
                        boolean error=false;
                        if(quantity.equals(""))
                        {
                            quantity_et.setError("Mandatory");
                            error=true;
                        }
                        if(rate.equals(""))
                        {
                            rate_et.setError("Mandatory");
                            error=true;
                        }

                        if(!error)
                        {
                            c_adapter.addItem(new BILL_ITEM(stk_grp_spinner.getSelectedItem().toString(), stk_item_spinner.getSelectedItem().toString(),quantity,unit_spinner.getSelectedItem().toString(),rate));
                            c_adapter.notifyDataSetChanged();
                            data.add(new BILL_ITEM(stk_grp_spinner.getSelectedItem().toString(), stk_item_spinner.getSelectedItem().toString(),quantity,unit_spinner.getSelectedItem().toString(),rate));
                            total+=Integer.parseInt(quantity)*Integer.parseInt(rate);
                            total_tv.setText(total.toString());
                            a_dialog.dismiss();
                        }
                    }
                });
            }
        });
        a_dialog.show();
    }

    public void submit(View view)
    {
        TextView dateET=(TextView) findViewById(R.id.dateEditText);
        EditText nameET=(EditText)findViewById(R.id.name_actv);
        TextView amtET=(TextView) findViewById(R.id.total_tv);
        final String date=(dateET.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
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
        if(amount.equals("0"))
        {
            Toast.makeText(getApplicationContext(),"No Item Added",Toast.LENGTH_SHORT).show();
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
                addNewBill(date,name,amount);
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
                addNewBill(date,name,amount);
            }

        }
    }

    private void addNewBill(String date,String name,String amount)
    {
        Toast.makeText(getApplicationContext(),"Bill Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean("SHOW_AGAIN",true))
            startActivity(new Intent(getApplicationContext(),NewBill.class));
        finish();
    }
}
