package com.example.root.raam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Accounts extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        getSupportActionBar().setTitle("Accounts");
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
        Intent i=new Intent(this,AccountView.class);
        i.putExtra("Name",getString(R.string.cash_in_hand));
        startActivity(i);
    }

    public void openBank(View view)
    {
        startActivity(new Intent(this,BankACC.class));
    }

    public void openExpense(View view)
    {
        Intent i=new Intent(this,AccountView.class);
        i.putExtra("Name","EXP");
        startActivity(i);
    }

    public void openSales(View view)
    {
        Intent i=new Intent(this,AccountView.class);
        i.putExtra("Name","SALES");
        startActivity(i);
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
