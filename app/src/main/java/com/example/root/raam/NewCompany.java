package com.example.root.raam;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class NewCompany extends AppCompatActivity
{
    String[] company_fields ={"Name","Password"};
    ArrayList<String> names;
    EditText name,pass,pass2;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company);
        name=(EditText)findViewById(R.id.name_et);
        pass=(EditText)findViewById(R.id.pass_et);
        pass2=(EditText)findViewById(R.id.confirm_et);
        db=new DatabaseHelper(this,"Company_List",company_fields.length,company_fields);
    }

    public void addCompany(View view)
    {
        if(names.contains(name.getText().toString()))
        {
            name.setError("Already Exists");
        }
        else
        {
            if(pass.equals(pass2))
            {
                db.insertData(new String[] {name.getText().toString(),pass.getText().toString()});
                finish();
            }
            else
            {
                pass2.setError("Password didn't match");
            }
        }

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Cursor c=db.getData();
        if (c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            names.add(c.getString(1));
        }
    }
}
