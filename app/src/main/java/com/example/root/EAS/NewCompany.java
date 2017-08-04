package com.example.root.EAS;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewCompany extends AppCompatActivity
{
    private ArrayList<String> names;
    private EditText name;
    private EditText pass;
    private EditText pass2;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company);
        String[] company_fields = this.getResources().getStringArray(R.array.Company_Fields);
        names=new ArrayList<>();
        name=(EditText)findViewById(R.id.name_et);
        pass=(EditText)findViewById(R.id.pass_et);
        pass2=(EditText)findViewById(R.id.confirm_et);
        pass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addCompany();
                return true;
            }
        });
        db=new DatabaseHelper(this,getString(R.string.Company_List), company_fields.length, company_fields);
        Cursor c=db.getData();
        if (c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            names.add(c.getString(1));
        }
    }
    public void addCompany(View view)
    {
        addCompany();
    }
    private void addCompany()
    {
        if(name.getText().toString().equals(""))
        {
            name.setError(getString(R.string.Required));
        }
        else if(names.contains(name.getText().toString()))
        {
            name.setError(getString(R.string.Exists));
        }
        else
        {
            if(pass.getText().toString().equals(""))
            {
                pass.setError(getString(R.string.Required));
            }
            else if(pass.getText().toString().equals(pass2.getText().toString()))
            {
                db.insertData(new String[] {name.getText().toString(),pass.getText().toString()});
                Toast.makeText(this,"Account successfully created",Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                pass2.setError("Password didn't match");
            }
        }

    }

}
