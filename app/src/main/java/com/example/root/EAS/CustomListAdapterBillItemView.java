package com.example.root.EAS;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

class CustomListAdapterBillItemView extends BaseAdapter
{
    private final ViewGroup nl=null;
    private final DecimalFormat dec_format=new DecimalFormat("#");
    private final ArrayList<BILL_ITEM> data;
    private final LayoutInflater layoutInflater;

    CustomListAdapterBillItemView(Context context, ArrayList<BILL_ITEM> data)
    {
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
        return convertView;
    }
}
