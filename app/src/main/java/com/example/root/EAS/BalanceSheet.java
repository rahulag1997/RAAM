package com.example.root.EAS;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;

public class BalanceSheet extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_sheet);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Balance_Sheet));
        showFAB();
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }
}
