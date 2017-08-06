package com.example.root.EAS;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseView extends BaseActivity
{
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean editMode=true;
    private int EXP_NUM,prev_total;
    private DatabaseHelper db_exp;
    private String[] acc_view_features;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_view);

        sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        EXP_NUM=Integer.parseInt(getIntent().getStringExtra("EXP_NUM"));

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Expense No. "+EXP_NUM);

        acc_view_features=getResources().getStringArray(R.array.Acc_View_Features);
        db_exp=new DatabaseHelper(this,getString(R.string.Expense)+"_"+getString(R.string.EXP),acc_view_features.length,acc_view_features);

        Cursor c=db_exp.getRowByNumber(EXP_NUM,"Expense");

        ((TextView)findViewById(R.id.date_line).findViewById(R.id.type_tv)).setText(getString(R.string.Date));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.type_tv)).setText(getString(R.string.Name));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.type_tv)).setText(getString(R.string.Amount));
        findViewById(R.id.notes_line).setVisibility(View.GONE);
        c.moveToNext();
        prev_total=Integer.parseInt(c.getString(2));
        ((TextView)findViewById(R.id.date_line).findViewById(R.id.value_tv)).setText(c.getString(5));
        ((TextView)findViewById(R.id.name_line).findViewById(R.id.value_tv)).setText(c.getString(1));
        ((TextView)findViewById(R.id.amt_line).findViewById(R.id.value_tv)).setText(c.getString(2));


        View customDialogView= View.inflate(getApplicationContext(),R.layout.confirm_dialog,null);
        final CheckBox cb=(CheckBox)customDialogView.findViewById(R.id.cb);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm?");
        builder.setView(customDialogView);
        builder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(cb.isChecked())
                {
                    Toast.makeText(getApplicationContext(),R.string.Confirmation_Disabled,Toast.LENGTH_SHORT).show();
                    editor=sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.CONFIRM_DEL),false);
                    editor.apply();
                }
                del();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_black_24dp);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editMode)
                {
                    startActivity(new Intent(getApplicationContext(),NewExpense.class).putExtra("EXP_NUM",EXP_NUM));
                    finish();
                }
                else
                {
                    if(sharedPreferences.getBoolean(getString(R.string.CONFIRM_DEL),true))
                        builder.create().show();
                    else
                    {
                        del();
                        finish();
                    }
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(editMode)
                    fab.setImageResource(R.drawable.ic_delete_black_24dp);
                else
                    fab.setImageResource(R.drawable.ic_edit_black_24dp);
                editMode=!editMode;
                return true;
            }
        });
    }

    private void del()
    {
        editExpense();
    }

    private void editExpense()
    {

        String[] expense_statement={"Expense",""+EXP_NUM};

        //insert in cash account
        DatabaseHelper db_cashInHand=new DatabaseHelper(this,getString(R.string.Cash)+"_"+getString(R.string.Cash_in_Hand),acc_view_features.length,acc_view_features);
        db_cashInHand.deleteVoucher(expense_statement);

        //update cash balance
        int updatedCash=sharedPreferences.getInt(getString(R.string.CASH_IN_HAND),0)+prev_total;
        editor=sharedPreferences.edit();
        editor.putInt("CASH IN HAND",updatedCash);
        editor.apply();

        //insert in expense account
        db_exp.deleteVoucher(expense_statement);

        //update expense balance
        int updatedExp=sharedPreferences.getInt(getString(R.string.EXP),0)-prev_total;
        editor=sharedPreferences.edit();
        editor.putInt(getString(R.string.EXP),updatedExp);
        editor.apply();
    }
}
