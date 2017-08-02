package com.example.root.raam;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

class CustomListAdapter extends BaseAdapter
{
    private ArrayList<DATA_ITEM> data;
    private LayoutInflater layoutInflater;
    int sno=1;
    private String acc_type;
    Context context;
    CustomListAdapter(Context context, ArrayList<DATA_ITEM> data,String type)
    {
        this.context=context;
        this.data=data;
        acc_type=type;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView==null)
        {
            convertView=layoutInflater.inflate(R.layout.data_item,null);
        }
        DATA_ITEM item=(DATA_ITEM)getItem(position);
        final TextView name_tv=(TextView)convertView.findViewById(R.id.data_name);
        TextView dr_tv=(TextView)convertView.findViewById(R.id.data_dr);
        TextView cr_tv=(TextView)convertView.findViewById(R.id.data_cr);
        TextView bal_tv=(TextView)convertView.findViewById(R.id.data_bal);
        TextView sno_tv=(TextView)convertView.findViewById(R.id.sno_tv);
        sno_tv.setText(Integer.toString(item.sno));
        name_tv.setText(item.name);
        dr_tv.setText(item.dr);
        cr_tv.setText(item.cr);
        bal_tv.setText(item.bal);
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(context,AccountView.class);
                i.putExtra(context.getString(R.string.Name),name_tv.getText());
                i.putExtra(context.getString(R.string.ACC_TYPE),acc_type);
                (context).startActivity(i);
            }
        });
        return convertView;
    }
}
