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
    String[] company_fields;

    ArrayList<String> names, pass;
    ArrayAdapter<String> adapter;
    DatabaseHelper db;
    AutoCompleteTextView name_actv;
    EditText pass_et;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        company_fields=this.getResources().getStringArray(R.array.Company_Fields);

        names=new ArrayList<>();
        pass=new ArrayList<>();

        db=new DatabaseHelper(this,getString(R.string.Company_List), company_fields.length, company_fields);

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
                startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("COMPANY",companyName));
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
