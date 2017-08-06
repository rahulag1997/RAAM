package com.example.root.EAS;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewPurchase extends BaseActivity implements CustomListAdapterBillItem.updateTotal
{
    private final DecimalFormat dec_format=new DecimalFormat("#");
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<String> names, stockGroups, stocks;
    private ArrayList<BILL_ITEM> data,data2;
    private final String[] units={"Dz","Pcs"};    //TODO replace with units
    private String[] acc_features, acc_view_features, item_fields, sgf;
    private CustomListAdapterBillItem c_adapter;
    private int PUR_NUM, total=0;
    private boolean editMode;
    private int prev_total;
    private String prev_name;
    private DatabaseHelper db_accList;
    private ArrayAdapter<String> stk_grp_adapter, stk_item_adapter;
    private AutoCompleteTextView ac_tv;
    private TextView total_tv, date_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);

        init();

        if(getIntent().hasExtra("PUR_NUM"))
        {
            editMode = true;
            PUR_NUM=getIntent().getIntExtra("PUR_NUM",1);
            setData();
        }
        else
        {
            editMode=false;
            PUR_NUM =sharedPreferences.getInt(getString(R.string.PUR_NUM),1);
        }

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Purchase No. "+ PUR_NUM);

        showFAB();
    }

    private void init()
    {
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        acc_features = getResources().getStringArray(R.array.Acc_Features);
        acc_view_features = getResources().getStringArray(R.array.Acc_View_Features);
        item_fields=getResources().getStringArray(R.array.Item_Fields);
        sgf=getResources().getStringArray(R.array.StockGroup_Features);

        names=new ArrayList<>();
        stockGroups=new ArrayList<>();
        stocks=new ArrayList<>();
        data=new ArrayList<>();
        data2=new ArrayList<>();

        getNames();

        date_tv =(TextView) findViewById(R.id.dateEditText);
        total_tv=(TextView)findViewById(R.id.total_tv);

        ac_tv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        ac_tv.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names));

        c_adapter= new CustomListAdapterBillItem(this,data,this);
        ((ListView)findViewById(R.id.item_list)).setAdapter(c_adapter);
    }

    private void getNames()
    {
        db_accList =new DatabaseHelper(this,getString(R.string.Account)+"_"+getString(R.string.Creditor), acc_features.length, acc_features);
        Cursor c= db_accList.sortByName();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                names.add(c.getString(1));
            }
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    private void setData()
    {
        DatabaseHelper db_purchases=new DatabaseHelper(this,getString(R.string.Purchases),acc_view_features.length,acc_view_features);
        Cursor c_purchases=db_purchases.getRowByNumber(PUR_NUM,"Purchase");
        c_purchases.moveToNext();
        date_tv.setText(c_purchases.getString(5));
        ac_tv.setText(c_purchases.getString(1));
        total_tv.setText(c_purchases.getString(2));

        DatabaseHelper db_purchase=new DatabaseHelper(getApplicationContext(),"Purchase_"+ PUR_NUM,item_fields.length,item_fields);
        Cursor c=db_purchase.getData();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                data2.add(new BILL_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5)));
                data.add(new BILL_ITEM(c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5)));
            }
        }
        c_adapter.notifyDataSetChanged();

        total=Integer.parseInt(c_purchases.getString(2));
        prev_total=total;
        prev_name=c_purchases.getString(1);
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

                        date_tv.setText(day + "-" + month + "-" + yearS);
                        date_tv.setError(null);
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
                            total_tv.setText(dec_format.format(total));
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
        final String date=(date_tv.getText()).toString();
        final String name=(ac_tv.getText()).toString();
        final String amount=(total_tv.getText()).toString();
        boolean showDialog=true;
        if(date.equals(""))
        {
            date_tv.setError(getString(R.string.Required));
            showDialog = false;
        }
        if(!(names.contains(name)))
        {
            ac_tv.setError("Not in data");
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
                addNewPurchase(date,name,amount);
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
                addNewPurchase(date,name,amount);
        }
    }

    private void addNewPurchase(String date,String name,String amount)
    {
        if(editMode)
        {
            editPurchase();
        }
        String[] purchase_statement={name,amount,"",getString(R.string.Purchase),date,""+PUR_NUM};
        Cursor c_acc=db_accList.getDataByName(name);
        c_acc.moveToNext();
        int balance=Integer.parseInt(c_acc.getString(4));
        int credit=Integer.parseInt(c_acc.getString(3));

        balance=balance+Integer.parseInt(amount);
        credit=credit+Integer.parseInt(amount);

        //update account list
        db_accList.updateData(new String[] {name,c_acc.getString(3),Integer.toString(credit),Integer.toString(balance),c_acc.getString(5)});

        //insert into party account
        DatabaseHelper db_party=new DatabaseHelper(this,getString(R.string.Creditor)+"_"+name,acc_view_features.length,acc_view_features);
        db_party.insertData(new String[] {"Purchase No. "+PUR_NUM,amount,"",getString(R.string.Purchase),date,""+PUR_NUM});


        DatabaseHelper db_purchases=new DatabaseHelper(this,getString(R.string.Purchases),acc_view_features.length,acc_view_features);

        String Message;
        if(editMode)
        {
            Message="Purchase Edited";
            db_purchases.updateVoucher(purchase_statement);
            new ResetStock().execute();
        }
        else
        {
            //insert into purchases
            db_purchases.insertData(purchase_statement);

            Message="Purchase Added";
            editor=sharedPreferences.edit();
            editor.putInt(getString(R.string.PUR_NUM),PUR_NUM+1);
            editor.apply();

        }

        new Updater().execute(date);

        Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_SHORT).show();
        if(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true))
            startActivity(new Intent(getApplicationContext(),NewPurchase.class));
        finish();
    }

    private void editPurchase()
    {
        Cursor c_acc=db_accList.getDataByName(prev_name);
        c_acc.moveToNext();
        int balance=Integer.parseInt(c_acc.getString(4));
        int credit=Integer.parseInt(c_acc.getString(3));

        balance=balance-prev_total;
        credit=credit-prev_total;

        //update account list
        db_accList.updateData(new String[] {prev_name,c_acc.getString(2),Integer.toString(credit),Integer.toString(balance),c_acc.getString(5)});

        //delete from party account
        DatabaseHelper db_party=new DatabaseHelper(this,getString(R.string.Creditor)+"_"+prev_name,acc_view_features.length,acc_view_features);
        db_party.deleteVoucher(new String[] {getString(R.string.Purchase),""+PUR_NUM});

    }

    private class Updater extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            DatabaseHelper db_pur = new DatabaseHelper(getApplicationContext(), "Purchase_" + PUR_NUM, item_fields.length, item_fields);
            if(editMode)
                db_pur.clearTable();
            String[] stk_features=getResources().getStringArray(R.array.Acc_Features);
            String[] item_fields=getResources().getStringArray(R.array.Item_Fields);
            String[] sgf=getResources().getStringArray(R.array.StockGroup_Features);
            DatabaseHelper db_purchase=new DatabaseHelper(getApplicationContext(),"Purchase_"+PUR_NUM,item_fields.length,item_fields);
            for (BILL_ITEM item:data)
            {
                //insert item into purchase
                db_purchase.insertData(new String[]{item.stk_grp,item.stk_item,item.quantity,item.unit,item.rate});

                //update stock list
                DatabaseHelper db_sg=new DatabaseHelper(getApplicationContext(),getString(R.string.SG)+"_"+item.stk_grp,sgf.length,sgf);
                Cursor c=db_sg.getData();
                if(c.getCount()!=0)
                {
                    while (c.moveToNext())
                        if(c.getString(1).equals(item.stk_item))
                            break;
                    int purchased=Integer.parseInt(c.getString(3))+Integer.parseInt(item.quantity);
                    int bal=Integer.parseInt(c.getString(4))+Integer.parseInt(item.quantity);
                    db_sg.updateData(new String[]{item.stk_item,c.getString(2),Integer.toString(purchased),Integer.toString(bal),item.rate});
                }

                //insert into stock data
                DatabaseHelper db_stock=new DatabaseHelper(getApplicationContext(),item.stk_grp+"_"+item.stk_item,stk_features.length,stk_features);
                db_stock.insertData(new String[]{"Purchase_"+PUR_NUM,item.quantity,"","Purchase",params[0]});
            }

            return null;
        }
    }

    private class ResetStock extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            for (BILL_ITEM item:data2)
            {
                //update stock list
                DatabaseHelper db_sg=new DatabaseHelper(getApplicationContext(),getString(R.string.SG)+"_"+item.stk_grp,sgf.length,sgf);
                Cursor c=db_sg.getDataByName(item.stk_item);
                if(c.getCount()!=0)
                {
                    c.moveToNext();
                    int purchased=Integer.parseInt(c.getString(3))-Integer.parseInt(item.quantity);
                    int bal=Integer.parseInt(c.getString(4))-Integer.parseInt(item.quantity);
                    db_sg.updateData(new String[]{item.stk_item,c.getString(2),Integer.toString(purchased),Integer.toString(bal),item.rate});
                }
                //insert into stock data
                //acc_features is the features that a stocks in a stock group will have
                DatabaseHelper db_stock=new DatabaseHelper(getApplicationContext(),item.stk_grp+"_"+item.stk_item,acc_features.length,acc_features);
                db_stock.deleteRowByBillName("Purchase_"+ PUR_NUM);
            }
            return null;
        }
    }

    @Override
    public void onProcessFilter(int change)
    {
        this.total-=change;
        total_tv.setText(dec_format.format(this.total));
    }
}
