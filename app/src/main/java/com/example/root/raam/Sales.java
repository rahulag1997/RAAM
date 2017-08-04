package com.example.root.raam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Sales extends BaseActivity
{
    TextView cash_amt,credit_amt;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        showFAB();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Sales));

        View cash_line=findViewById(R.id.Cash_Line);
        View credit_line=findViewById(R.id.Credit_Line);
        TextView cash_type=(TextView)cash_line.findViewById(R.id.type_tv);
        cash_amt=(TextView)cash_line.findViewById(R.id.value_tv);
        TextView credit_type=(TextView)credit_line.findViewById(R.id.type_tv);
        credit_amt=(TextView)credit_line.findViewById(R.id.value_tv);

        cash_type.setText(R.string.Cash_Sales);
        credit_type.setText(R.string.Credit_Sales);

        cash_type.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),AccountView.class).putExtra(getString(R.string.Name),getString(R.string.Cash)).putExtra(getString(R.string.ACC_TYPE),getString(R.string.Sales)));

            }
        });

        credit_type.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),AccountView.class).putExtra(getString(R.string.Name),getString(R.string.Credit)).putExtra(getString(R.string.ACC_TYPE),getString(R.string.Sales)));

            }
        });
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    @Override
    protected void onResume()
    {
        DecimalFormat dec_format=new DecimalFormat("#");
        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        cash_amt.setText(dec_format.format(sharedPreferences.getInt(getString(R.string.SALES_CASH),0)));
        credit_amt.setText(dec_format.format(sharedPreferences.getInt(getString(R.string.SALES_CREDIT),0)));
        super.onResume();
    }
}
