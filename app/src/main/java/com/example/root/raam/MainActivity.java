package com.example.root.raam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class MainActivity extends BaseActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFAB();

        String[] acc_view_features = this.getResources().getStringArray(R.array.Acc_View_Features);

        SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        if(!(sharedPreferences.getBoolean(getString(R.string.CASH_CREATED),false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.Opening_Balance),"0","",getString(R.string.OB),"01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(getString(R.string.CASH_CREATED),true);
            editor.putInt(getString(R.string.CASH_IN_HAND),0);
            editor.apply();
        }
        if(!(sharedPreferences.getBoolean(getString(R.string.SALES_CREATED),false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.Sales)+"_"+getString(R.string.Sales), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.Opening_Balance),"0","",getString(R.string.OB),"01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(getString(R.string.SALES_CREATED),true);
            editor.putInt(getString(R.string.SALES),0);
            editor.apply();
        }
        if(!(sharedPreferences.getBoolean(getString(R.string.EXP_CREATED),false)))
        {
            DatabaseHelper db=new DatabaseHelper(this,getString(R.string.Expense)+"_"+getString(R.string.EXP), acc_view_features.length, acc_view_features);
            db.insertData(new String[] {getString(R.string.Opening_Balance),"0","",getString(R.string.OB),"01-01-2017"});
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(getString(R.string.EXP_CREATED),true);
            editor.putInt(getString(R.string.EXP),0);
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

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Exit?");
        builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
