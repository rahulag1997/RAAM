package com.example.root.raam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

public class MainActivity extends BaseActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFAB();

        String[] acc_view_features = this.getResources().getStringArray(R.array.acc_view_features);

        SharedPreferences sharedPreferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(!(sharedPreferences.getBoolean("CASH_CREATED",false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.cash_in_hand), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.ob),"0","","OB","01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("CASH_CREATED",true);
            editor.putInt("CASH_IN_HAND",0);
            editor.apply();
        }
        if(!(sharedPreferences.getBoolean("SALES_CREATED",false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.sales), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.ob),"0","","OB","01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("SALES_CREATED",true);
            editor.putInt("SALES",0);
            editor.apply();
        }
        if(!(sharedPreferences.getBoolean("EXP_CREATED",false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.exp), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.ob),"0","","OB","01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("EXP_CREATED",true);
            editor.putInt("EXP",0);
            editor.apply();
        }
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    public void openAccounts(View view)
    {
        Intent activityOpener = new Intent(this,Accounts.class);
        startActivity(activityOpener);
    }

    public void openStock(View view)
    {
        Intent activityOpener = new Intent(this,Stock.class);
        startActivity(activityOpener);
    }

    public void openBS(View view)
    {
        Intent activityOpener = new Intent(this,BalanceSheet.class);
        startActivity(activityOpener);
    }
}
