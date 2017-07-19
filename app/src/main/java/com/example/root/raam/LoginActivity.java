package com.example.root.raam;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{
    String[] company_fields ={"Name","Password"};

    ArrayList<String> names;
    ArrayList<String> pass;
    ArrayAdapter<String> adapter;
    DatabaseHelper db;
    AutoCompleteTextView name_actv;
    EditText pass_et;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DatabaseHelper db=new DatabaseHelper(this,"Company_List", company_fields.length, company_fields);

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);

        name_actv=(AutoCompleteTextView)findViewById(R.id.name_actv);
        name_actv.setAdapter(adapter);
        pass_et=(EditText)findViewById(R.id.pass_et);
    }

    public void login(View view)
    {
        String companyName=name_actv.getText().toString();
        String password=pass_et.getText().toString();
        if(names.contains(companyName))
        {
            if(pass.get(names.indexOf(companyName)).equals(password))
            {
                startActivity(new Intent(this,MainActivity.class).putExtra("COMPANY",companyName).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
            else
            {
                pass_et.setError("Incorrect Password");
            }
        }
        else
        {
            name_actv.setError("Company does not exist in Database");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        names.clear();
        pass.clear();
        getData();
        adapter.notifyDataSetChanged();
    }

    private void getData()
    {
        Cursor c=db.getData();
        if(c.getCount()==0)
            return;
        while (c.moveToNext())
        {
            names.add(c.getString(1));
            pass.add(c.getString(2));
        }
    }


}
