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
import java.util.Calendar;

public class NewBill extends BaseActivity implements CustomListAdapterBillItem.updateTotal
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> names, stockGroups, stocks;
    ArrayList<BILL_ITEM> data;

    String[] units={"Dz","Pcs"};    //TODO replace with units

    CustomListAdapterBillItem c_adapter;
    Integer total=0;
    TextView total_tv;

    DatabaseHelper db_accList;

    ArrayAdapter<String> stk_grp_adapter, stk_item_adapter;

    String[] acc_features, acc_view_features;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.New_Bill));
        showFAB();

        names=new ArrayList<>();
        stockGroups=new ArrayList<>();
        stocks=new ArrayList<>();
        data=new ArrayList<>();

        acc_features = getResources().getStringArray(R.array.Acc_Features);
        acc_view_features = getResources().getStringArray(R.array.Acc_View_Features);

        db_accList =new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Debtor), acc_features.length, acc_features);
        Cursor c= db_accList.sortByName();
        names.add("Cash");

        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                names.add(c.getString(1));
            }
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);
        AutoCompleteTextView actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        actv.setAdapter(adapter);

        ListView list=(ListView)findViewById(R.id.item_list);
        c_adapter= new CustomListAdapterBillItem(this,data,this);
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

    public void addItem(View view)
    {
        View new_item=View.inflate(this,R.layout.new_item,null);
        final Spinner stk_grp_spinner=(Spinner)new_item.findViewById(R.id.stock_group_spinner);
        stk_grp_adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,stockGroups);
        stk_grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stk_grp_spinner.setAdapter(stk_grp_adapter);

        getStockGroup();

        final Spinner stk_item_spinner=(Spinner)new_item.findViewById(R.id.stock_spinner);
        stk_item_adapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,stocks);
        stk_item_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stk_item_spinner.setAdapter(stk_item_adapter);

        stk_grp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getStock(stockGroups.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        final Spinner unit_spinner=(Spinner)new_item.findViewById(R.id.unit_spinner);
        ArrayAdapter<String> unit_adapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,units);
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
            public void onShow(DialogInterface dialog)
            {
                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String quantity=quantity_et.getText().toString();
                        String rate=rate_et.getText().toString();
                        boolean error=false;
                        if(quantity.equals(""))
                        {
                            quantity_et.setError(getString(R.string.Required));
                            error=true;
                        }
                        if(rate.equals(""))
                        {
                            rate_et.setError(getString(R.string.Required));
                            error=true;
                        }
                        if(stk_grp_spinner.getSelectedItem()==null)
                        {
                            Toast.makeText(getApplicationContext(),"No group selected",Toast.LENGTH_SHORT).show();
                            error=true;
                        }
                        if(stk_item_spinner.getSelectedItem()==null)
                        {
                            Toast.makeText(getApplicationContext(),"No item selected",Toast.LENGTH_SHORT).show();
                            error=true;
                        }


                        if(!error)
                        {
                            c_adapter.addItem(new BILL_ITEM(stk_grp_spinner.getSelectedItem().toString(), stk_item_spinner.getSelectedItem().toString(),quantity,unit_spinner.getSelectedItem().toString(),rate));
                            c_adapter.notifyDataSetChanged();
                            //data.add(new BILL_ITEM(stk_grp_spinner.getSelectedItem().toString(), stk_item_spinner.getSelectedItem().toString(),quantity,unit_spinner.getSelectedItem().toString(),rate));
                            total+=Integer.parseInt(quantity)*Integer.parseInt(rate);
                            total_tv.setText(Integer.toString(total));
                            a_dialog.dismiss();
                        }
                    }
                });
            }
        });
        a_dialog.show();
    }

    private void getStockGroup()
    {
        String[] fields=this.getResources().getStringArray(R.array.StockGroupListFeatures);
        stockGroups.clear();
        DatabaseHelper db=new DatabaseHelper(this,getString(R.string.SGL),fields.length,fields);
        Cursor c=db.sortByName();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                stockGroups.add(c.getString(1));
            }
        }
        stk_grp_adapter.notifyDataSetChanged();

    }

    private void getStock(String stockGroup)
    {
        stocks.clear();
        String[] fields=this.getResources().getStringArray(R.array.StockGroup_Features);
        DatabaseHelper db=new DatabaseHelper(this,getString(R.string.StockGroup)+"_"+stockGroup,fields.length,fields);
        Cursor c=db.sortByName();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
                stocks.add(c.getString(1));
        }
        stk_item_adapter.notifyDataSetChanged();
    }

    public void submit(View view)
    {
        TextView dateTV=(TextView) findViewById(R.id.dateEditText);
        EditText nameET=(EditText)findViewById(R.id.name_actv);
        TextView amtET=(TextView) findViewById(R.id.total_tv);
        final String date=(dateTV.getText()).toString();
        final String name=(nameET.getText()).toString();
        final String amount=(amtET.getText()).toString();
        boolean showDialog=true;
        if(date.equals(""))
        {
            dateTV.setError(getString(R.string.Required));
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
                addNewBill(date,name,amount);
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
                addNewBill(date,name,amount);
        }
    }

    private void addNewBill(String date,String name,String amount)
    {
        //TODO also update stock in background
        //TODO add bill details in background

        Cursor cursor_accList= db_accList.getData();
        while (cursor_accList.moveToNext())
        {
            if(cursor_accList.getString(1).equals(name))
                break;
        }
        int balance=Integer.parseInt(cursor_accList.getString(4));
        int credit=Integer.parseInt(cursor_accList.getString(3));

        balance=balance+Integer.parseInt(amount);
        credit=credit+Integer.parseInt(amount);

        //Update in acc_list
        db_accList.updateData(new String[] {name,cursor_accList.getString(2),Integer.toString(credit),Integer.toString(balance),cursor_accList.getString(5)});
        DatabaseHelper db_party;

        //insert in party account
        if(name.equals("Cash"))
            db_party=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        else
            db_party=new DatabaseHelper(this,getString(R.string.Debtor)+"_"+name,acc_view_features.length,acc_view_features);

        db_party.insertData(new String[] {"Bill on "+date,amount,"",getString(R.string.Bill),date});

        //insert in sales account
        DatabaseHelper db_sales=new DatabaseHelper(this,getString(R.string.Sales)+"_"+getString(R.string.Sales),acc_view_features.length,acc_view_features);
        db_sales.insertData(new String[] {name+" "+date,amount,"",getString(R.string.Bill),date});

        //update sales balance
        editor=sharedPreferences.edit();
        int updatedSales=sharedPreferences.getInt(getString(R.string.Sales),0)+Integer.parseInt(amount);
        editor.putInt(getString(R.string.Sales),updatedSales);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Bill Added",Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewBill.class));
        finish();
    }

    @Override
    public void onProcessFilter(int change)
    {
        this.total-=change;
        total_tv.setText(Integer.toString(this.total));
    }
}
