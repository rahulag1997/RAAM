package com.example.root.raam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void login(View view)
    {
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void create(View view)
    {
    }

    public void exit(View view)
    {
        finish();
    }
}
