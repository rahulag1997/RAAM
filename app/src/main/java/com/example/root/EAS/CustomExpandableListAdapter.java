package com.example.root.EAS;


import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.text.DecimalFormat;

class CustomExpandableListAdapter extends BaseExpandableListAdapter
{
    private final SparseArray<Stock_group> stock_groups;
    private final LayoutInflater inflater;
    private final ViewGroup nl=null;
    private final DecimalFormat dec_format=new DecimalFormat("#");

    CustomExpandableListAdapter(Activity act, SparseArray<Stock_group> stock_groups)
    {
        this.stock_groups = stock_groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return stock_groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        final DATA_ITEM children = (DATA_ITEM) getChild(groupPosition, childPosition);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.data_item, nl);
        }
        TextView name_tv = (TextView) convertView.findViewById(R.id.data_name);
        TextView dr_tv=(TextView)convertView.findViewById(R.id.data_dr);
        TextView cr_tv=(TextView)convertView.findViewById(R.id.data_cr);
        TextView bal_tv =  (TextView) convertView.findViewById(R.id.data_bal);
        TextView sno_tv=(TextView)convertView.findViewById(R.id.sno_tv);
        sno_tv.setText(dec_format.format(childPosition+1));
        name_tv.setText(children.name);
        dr_tv.setText(children.dr);
        cr_tv.setText(children.cr);
        bal_tv.setText(children.bal);
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO open stock View
                /*
                Intent i=new Intent(activity,StockView.class);
                i.putExtra("Name",children.name);
                (activity).startActivity(i);*/
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return stock_groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return stock_groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return stock_groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.stock_group, nl);
        }
        Stock_group group = (Stock_group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
