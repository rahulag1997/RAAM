package com.example.root.EAS;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class Settings extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Settings");
        showFAB();

        SharedPreferences sharedPreferences=this.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        CheckBox confirm=(CheckBox)findViewById(R.id.confirm_check);
        confirm.setChecked(sharedPreferences.getBoolean(getString(R.string.SHOW_DIALOG),true));

        confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                    editor.putBoolean(getString(R.string.SHOW_DIALOG),isChecked);
                    editor.apply();
            }
        });

        CheckBox repeat=(CheckBox)findViewById(R.id.redirect_check);
        repeat.setChecked(sharedPreferences.getBoolean(getString(R.string.SHOW_AGAIN),true));
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ((TextView)findViewById(R.id.guide_tv)).setText(R.string.NewForm);
                else
                    ((TextView)findViewById(R.id.guide_tv)).setText(R.string.GoBack);

                editor.putBoolean(getString(R.string.SHOW_AGAIN),isChecked);
                editor.apply();
            }
        });
    }

    private void showFAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

}