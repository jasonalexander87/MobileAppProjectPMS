package com.iasonas.melionis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity {

    TextView View;
    EditText Server;
    EditText PGTopics;
    EditText PGPosts;

    Button Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        View = (TextView) findViewById(R.id.textViewPrefs);
        Server = (EditText) findViewById(R.id.editTextSERVER);
        PGTopics = (EditText) findViewById(R.id.editTextPAGINATIONTOPICS);
        PGPosts = (EditText) findViewById(R.id.editTextPAGINATIONPOSTS);
        Save = (Button) findViewById(R.id.buttonSavePrefs);

        Save.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                String ServerA = Server.getText().toString();
                String PGTopicsN = PGTopics.getText().toString();
                String PGPostsN = PGPosts.getText().toString();
                setPrefs(ServerA, PGTopicsN, PGPostsN);


            }
        });
    }

    public boolean setPrefs(String server, String pgtN, String pgpN) {

        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString("PGTN", pgtN);
        editor.putString("PGPN", pgpN);
        editor.putString("SERVER", server);

        editor.apply();

        return true;
    }
}
