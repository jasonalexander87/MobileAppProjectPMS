package com.iasonas.melionis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Intent LogInActivity;
    Intent RegisterActivity;
    Intent ReadForumsActivity;
    Intent PrefsActivity;

    Button LogInButton;
    Button RegisterButton;
    Button PrefsButton;
    Button ReadForumsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogInActivity = new Intent(this,LogInActivity.class);
        RegisterActivity = new Intent(this,RegisterActivity.class);
        ReadForumsActivity = new Intent(this,ReadForumsActivity.class);
        PrefsActivity = new Intent(this,PrefsActivity.class);

        LogInButton = (Button)findViewById(R.id.buttonLogin);
        RegisterButton = (Button)findViewById(R.id.buttonRegisterActivity);
        PrefsButton = (Button)findViewById(R.id.buttonPrefs);
        ReadForumsButton = (Button)findViewById(R.id.buttonForums);

        LogInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View _view) {

                startActivity(LogInActivity);
                finish();

            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View _view) {

                startActivity(RegisterActivity);
                finish();

            }
        });

        PrefsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View _view) {

                startActivity(PrefsActivity);
                finish();

            }
        });

        ReadForumsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View _view) {

                startActivity(ReadForumsActivity);
                finish();

            }
        });





    }
}