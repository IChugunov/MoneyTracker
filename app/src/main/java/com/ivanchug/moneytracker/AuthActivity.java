package com.ivanchug.moneytracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class AuthActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LsApp) getApplication()).setLoggedIn(true);
                finish();
            }
        });
    }

}
