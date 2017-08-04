package com.example.root.raam;


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
import java.util.ArrayList;

class CustomListAdapterBillItemView extends BaseAdapter
{
    private ArrayList<BILL_ITEM> data;
    private LayoutInflater layoutInflater;
    Context context;

    CustomListAdapterBillItemView(Context context, ArrayList<BILL_ITEM> data)
    {
        this.context=context;
        this.data=data;
        layoutInflater = LayoutInflater.from(context);
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
            convertView=layoutInflater.inflate(R.layout.bill_title,null);
        }
        BILL_ITEM bill_item=(BILL_ITEM) getItem(position);
        TextView sno_tv=(TextView)convertView.findViewById(R.id.sno_tv);
        final TextView name_tv=(TextView)convertView.findViewById(R.id.name_tv);
        TextView qty_tv=(TextView)convertView.findViewById(R.id.qty_tv);
        TextView unit_tv=(TextView)convertView.findViewById(R.id.unit_tv);
        TextView rate_tv=(TextView)convertView.findViewById(R.id.rate_tv);
        TextView amt_tv=(TextView)convertView.findViewById(R.id.amt_tv);
        sno_tv.setText(Integer.toString(position+1));
        name_tv.setText(bill_item.stk_grp+" "+bill_item.stk_item);
        qty_tv.setText(bill_item.quantity);
        unit_tv.setText(bill_item.unit);
        rate_tv.setText(bill_item.rate);
        amt_tv.setText(Integer.toString(Integer.parseInt(bill_item.quantity)*Integer.parseInt(bill_item.rate)));
        return convertView;
    }
}
