package com.example.root.raam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

public class MainActivity extends BaseActivity
{
    private final boolean hasFAB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
