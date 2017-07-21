package com.example.root.raam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;

public class Accounts extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.Accounts));
        showFAB();
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),NewAccount.class));
            }
        });
    }

    public void openCash(View view)
    {
        startActivity(new Intent(this,AccountView.class).putExtra(getString(R.string.Name),getString(R.string.Cash_in_Hand)).putExtra(getString(R.string.ACC_TYPE),getString(R.string.Cash)));
    }

    public void openBank(View view)
    {
        startActivity(new Intent(this,BankACC.class));
    }

    public void openExpense(View view)
    {
        startActivity(new Intent(this,AccountView.class).putExtra(getString(R.string.Name),getString(R.string.EXP)).putExtra(getString(R.string.ACC_TYPE),getString(R.string.Expense)));
    }

    public void openSales(View view)
    {
        startActivity(new Intent(this,AccountView.class).putExtra(getString(R.string.Name),getString(R.string.Sales)).putExtra(getString(R.string.ACC_TYPE),getString(R.string.Sales)));
    }

    public void openSC(View view)
    {
        startActivity(new Intent(this,SundryCreditors.class));
    }

    public void openSD(View view)
    {
        startActivity(new Intent(this,SundryDebtors.class));
    }
}
