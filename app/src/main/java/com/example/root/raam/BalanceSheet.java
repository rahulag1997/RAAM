package com.example.root.raam;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BalanceSheet extends BaseActivity
{
    private final boolean hasFAB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_sheet);
        getSupportActionBar().setTitle("Balance Sheet");
        showFAB();
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(hasFAB)
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //add command

                }
            });
        }
        else
        {
            fab.hide();
        }
    }
}
