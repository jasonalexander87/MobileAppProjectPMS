package com.iasonas.melionis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CreateTopicActivity extends AppCompatActivity {

    Button Topic;
    EditText title;
    EditText description;
    String IP;
    String location;
    String uName;
    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtopic);

        Intent Intent = getIntent();
        String forum = Intent.getStringExtra("forum");

        uName = getUname();
        cookie = getCookie();

        Intent getLoc = new Intent(this, GetLocationService.class);
        getLoc.putExtra("listener", new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);

                if (resultCode == Activity.RESULT_OK) {

                    location = resultData.getString("result");

                }
            }
        });

        startService(getLoc);

        Topic = (Button)findViewById(R.id.buttonCreatTopic);
        title = (EditText) findViewById(R.id.editTextTopicTitle);
        description = (EditText) findViewById(R.id.editTextTopicDescription);
        IP = getIP();

        Topic.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                String topicTiltle = title.getText().toString();
                String topicDescription = description.getText().toString();

                new CreateTopicsTask().execute(IP, forum, uName, topicTiltle, topicDescription, location, cookie);

            }
        });

    }

    private class CreateTopicsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result;
            Socket sock;
            PrintStream output;
            BufferedReader input;
            String request;


            request = "createTopic" + "/"+ strings[1] + "/"+ strings[2] + "/" + strings[3] + "/" +strings[4] + "/" + strings[5] + "/" + strings[6];

            try {
                InetAddress add = InetAddress.getByName(strings[0]);
                sock = new Socket(add, 9999);
                output = new PrintStream(sock.getOutputStream());
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                output.print(request);
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


        }


    }

    public String getUname() {


        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        String uNAME = sharedPrefs.getString("COOKIE", "0");

        return uNAME.split("/")[0];

    }

    public String getIP() {

        String IPAddress;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        IPAddress = sharedPrefs.getString("server", "0.0.0.0");

        return IPAddress;
    }

    public String getCookie() {

        String cookie;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        cookie = sharedPrefs.getString("COOKIE", "NULL");

        return cookie;
    }

}
