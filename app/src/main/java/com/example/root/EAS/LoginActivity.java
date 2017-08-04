package com.example.root.EAS;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{

    private ArrayList<String> names;
    private ArrayList<String> pass;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper db;
    private AutoCompleteTextView name_ac_tv;
    private EditText pass_et;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String[] company_fields = this.getResources().getStringArray(R.array.Company_Fields);

        names=new ArrayList<>();
        pass=new ArrayList<>();

        db=new DatabaseHelper(this,getString(R.string.Company_List), company_fields.length, company_fields);

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,names);

        name_ac_tv =(AutoCompleteTextView)findViewById(R.id.name_actv);
        name_ac_tv.setAdapter(adapter);
        pass_et=(EditText)findViewById(R.id.pass_et);
        name_ac_tv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                pass_et.requestFocus();
            }
        });
        pass_et.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                Login();
                return true;
            }
        });
    }

    private void Login()
    {
        String companyName= name_ac_tv.getText().toString();
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
            name_ac_tv.setError("Company does not exist in Database");
        }
    }

    public void login(View view)
    {
        Login();
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
