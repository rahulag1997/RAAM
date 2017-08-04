package com.example.root.EAS;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

class CustomListAdapterBillItem extends BaseAdapter
{
    private final ViewGroup nl=null;
    private final DecimalFormat dec_format=new DecimalFormat("#");
    private final ArrayList<String> stockGroups;
    private final ArrayList<String> stocks;
    private final String[] units={"Dz","Pcs"};
    private final ArrayList<BILL_ITEM> data;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private int change=0;
    private final updateTotal myUpdater;
    private final String[] stockGroupListFeatures;
    private final String[] stockGroupFeatures;

    interface updateTotal
    {
        void onProcessFilter(int change);
    }

    CustomListAdapterBillItem(Context context, ArrayList<BILL_ITEM> data, updateTotal callback)
    {
        myUpdater=callback;
        this.context=context;
        this.data=data;
        layoutInflater = LayoutInflater.from(context);
        stockGroups=new ArrayList<>();
        stocks=new ArrayList<>();
        stockGroupListFeatures=context.getResources().getStringArray(R.array.StockGroupListFeatures);
        stockGroupFeatures=context.getResources().getStringArray(R.array.StockGroup_Features);
        getData();
    }

    private void getData()
    {
        DatabaseHelper db=new DatabaseHelper(context,context.getString(R.string.SGL),stockGroupListFeatures.length,stockGroupListFeatures);
        Cursor c=db.getData();
        if(c.getCount()!=0)
        {
            while (c.moveToNext())
            {
                stockGroups.add(c.getString(1));
            }
        }
    }

    void addItem(BILL_ITEM item)
    {
        data.add(item);
        notifyDataSetChanged();
    }
    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView==null)
        {
            convertView=layoutInflater.inflate(R.layout.bill_title,nl);
        }
        BILL_ITEM bill_item=(BILL_ITEM) getItem(position);
        TextView sno_tv=(TextView)convertView.findViewById(R.id.sno_tv);
        final TextView name_tv=(TextView)convertView.findViewById(R.id.name_tv);
        TextView qty_tv=(TextView)convertView.findViewById(R.id.qty_tv);
        TextView unit_tv=(TextView)convertView.findViewById(R.id.unit_tv);
        TextView rate_tv=(TextView)convertView.findViewById(R.id.rate_tv);
        TextView amt_tv=(TextView)convertView.findViewById(R.id.amt_tv);
        sno_tv.setText(dec_format.format(position+1));
        name_tv.setText(bill_item.stk_grp+" "+bill_item.stk_item);
        qty_tv.setText(bill_item.quantity);
        unit_tv.setText(bill_item.unit);
        rate_tv.setText(bill_item.rate);
        amt_tv.setText(dec_format.format(Integer.parseInt(bill_item.quantity)*Integer.parseInt(bill_item.rate)));
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BILL_ITEM selected_item=(BILL_ITEM)getItem(position);

                View new_item=View.inflate(context,R.layout.new_item,null);

                final Spinner stk_grp_spinner=(Spinner)new_item.findViewById(R.id.stock_group_spinner);
                ArrayAdapter<String> stk_grp_adapter=new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,stockGroups);
                stk_grp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stk_grp_spinner.setAdapter(stk_grp_adapter);

                final Spinner stk_item_spinner=(Spinner)new_item.findViewById(R.id.stock_spinner);
                final ArrayAdapter<String> stk_item_adapter= new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,stocks);
                stk_item_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stk_item_spinner.setAdapter(stk_item_adapter);

                stk_grp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        DatabaseHelper db_stkGrp=new DatabaseHelper(context,context.getString(R.string.StockGroup)+"_"+stockGroups.get(position),stockGroupFeatures.length,stockGroupFeatures);
                        Cursor c=db_stkGrp.getData();
                        if(c.getCount()!=0)
                        {
                            while (c.moveToNext())
                            {
                                stocks.add(c.getString(1));
                            }
                        }
                        stk_item_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });

                final EditText quantity_et=(EditText)new_item.findViewById(R.id.quantity_et);

                final Spinner unit_spinner=(Spinner)new_item.findViewById(R.id.unit_spinner);
                ArrayAdapter<String> unit_adapter= new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,units);
                unit_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unit_spinner.setAdapter(unit_adapter);

                final EditText rate_et=(EditText)new_item.findViewById(R.id.rate_et);
                //TODO fix spinner item
                //stk_grp_spinner.setSelection(testStock.getIndex(selected_item.stk_grp);
                //stk_item_spinner.setSelection(testStock_list.getIndex(selected_item.stk_item);
                quantity_et.setText(selected_item.quantity);
                //unit_spinner.setSelection(units.getIndex(selected_item.unit);
                rate_et.setText(selected_item.rate);

                change=Integer.parseInt(selected_item.quantity)*Integer.parseInt(selected_item.rate);

                final AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Edit Item");
                builder.setView(new_item);

                builder.setPositiveButton("Edit",null);
                builder.setNegativeButton("Delete",null);
                final AlertDialog a_dialog=builder.create();
                a_dialog.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {

                        final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String quantity=quantity_et.getText().toString();
                                String rate=rate_et.getText().toString();
                                boolean error=false;
                                if(quantity.equals(""))
                                {
                                    quantity_et.setError(context.getString(R.string.Required));
                                    error=true;
                                }
                                if(rate.equals(""))
                                {
                                    rate_et.setError(context.getString(R.string.Required));
                                    error=true;
                                }

                                if(!error)
                                {
                                    data.set(position,new BILL_ITEM(stk_grp_spinner.getSelectedItem().toString(), stk_item_spinner.getSelectedItem().toString(),quantity,unit_spinner.getSelectedItem().toString(),rate));
                                    notifyDataSetChanged();
                                    change-=Integer.parseInt(quantity)*Integer.parseInt(rate);
                                    myUpdater.onProcessFilter(change);
                                    a_dialog.dismiss();
                                }
                            }
                        });

                        final Button dButton=((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        dButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                data.remove(position);
                                notifyDataSetChanged();
                                myUpdater.onProcessFilter(change);
                                a_dialog.dismiss();
                            }
                        });
                    }
                });
                a_dialog.show();
            }
        });
        return convertView;
    }
}
