package com.example.root.EAS;


import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptView extends BaseActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_view);
        final int RPT_NUM=Integer.parseInt(getIntent().getStringExtra("RPT_NUM"));
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Receipt No. "+RPT_NUM);
        String[] acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        DatabaseHelper db=new DatabaseHelper(this,"Receipts",acc_view_features.length,acc_view_features);
        Cursor c=db.getRow(RPT_NUM);

        ((TextView)findViewById(R.id.date_line).findViewById(R.id.type_tv)).setText(getString(R.string.Date));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.type_tv)).setText(getString(R.string.Name));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.type_tv)).setText(getString(R.string.Amount));
        ((TextView)findViewById(R.id.notes_line).findViewById(R.id.type_tv)).setText(getString(R.string.Additional_Notes));
        c.moveToNext();
        ((TextView)findViewById(R.id.date_line).findViewById(R.id.value_tv)).setText(c.getString(5));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.value_tv)).setText(c.getString(1));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.value_tv)).setText(c.getString(2));
        ((TextView)findViewById(R.id.notes_line).findViewById(R.id.value_tv)).setText(c.getString(3));

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_black_24dp);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewReceipt.class).putExtra("RPT_NUM",RPT_NUM));
                finish();
            }
        });
    }
}