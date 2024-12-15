package com.iasonas.melionis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LogInActivity extends AppCompatActivity {

    TextView View;
    EditText Name;
    EditText Pass;
    Button LogIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View = (TextView) findViewById(R.id.textViewLogIn);
        Name = (EditText) findViewById(R.id.editTexTName);
        Pass = (EditText) findViewById(R.id.editTextPassword);
        LogIN = (Button) findViewById(R.id.buttonLogIn);

        LogIN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View _view) {

                String NameS = Name.getText().toString();
                String PassS = Pass.getText().toString();
                String IpAdd = getIP();

                View.setText("LogingIn");
                LogIN.setEnabled(false);
                new LogInTask().execute(IpAdd, NameS, PassS);

            }
        });

    }

    private class LogInTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result;
            Socket sock;
            PrintStream output;
            BufferedReader input;

            String LogIn = "login" + "/" + strings[1] + "/" + strings[2];

            try {
                InetAddress add = InetAddress.getByName(strings[0]);
                sock = new Socket(add, 9999);
                output = new PrintStream(sock.getOutputStream());
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                output.print(LogIn);
                result = input.readLine();

                output.close();
                input.close();
                sock.close();

                return result;

            } catch (UnknownHostException e) {
                e.printStackTrace();
                return "ERROR";
            } catch (IOException e) {
                e.printStackTrace();
                return "ERROR";
            }

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if(!s.equals("ERROR")){
                setCookie(s);
                View.setText("SUCCESS");
            }
            View.setText("ERROR");
            LogIN.setEnabled(true);
        }
    }

    public boolean setCookie(String cookie) {

        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString("COOKIE", cookie);

        editor.apply();

        return true;
    }

    public String getIP() {

        String IPAddress;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        IPAddress = sharedPrefs.getString("server", "0.0.0.0");

        return IPAddress;
    }

}
