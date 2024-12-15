package com.iasonas.melionis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

public class ReadForumsActivity extends AppCompatActivity {

    LinearLayout ForumsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readforums);

        ForumsView = (LinearLayout) findViewById(R.id.forumsLayout);
        String IpAdd = getIP();
        String cookie = getCookie();
        new ReadForumsTask().execute(IpAdd, cookie);

    }

    private class ReadForumsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result;
            Socket sock;
            PrintStream output;
            BufferedReader input;
            String request;

            request = "getForums" + "/" + strings[1];

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

            updateView(s);
        }


    }

    public void updateView(String data) {

        JSONObject forums;
        JSONArray forums2;
        int counter = 0;

        try {
            forums = new JSONObject(data);
            forums2 = forums.getJSONArray("forums");

            while(counter < forums2.length()) {
                JSONObject current = forums2.getJSONObject(counter);
                String title = current.getString("title");
                String location = current.getString("location");
                String description = current.getString("description");

                counter++;

                String text = "TITLE:" + title + "\n" + "LOCATION:" + location + "\n" + "DESCRIPTION:" + description;
                TextView tv = new TextView(this);
                tv.setText(text);
                tv.setClickable(true);
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent tomap = new Intent(getApplicationContext(), MapActivity.class);
                        tomap.putExtra("location", location);

                        startActivity(tomap);

                        return false;
                    }
                });
                tv.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View _view) {

                        Intent toTopics = new Intent(getApplicationContext(), ReadTopicsActivity.class);
                        toTopics.putExtra("forum", title);

                        startActivity(toTopics);

                    }
                });
                ForumsView.addView(tv);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getCookie() {

        String cookie;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        cookie = sharedPrefs.getString("COOKIE", "NULL");

        return cookie;
    }

    public String getIP() {

        String IPAddress;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        IPAddress = sharedPrefs.getString("server", "0.0.0.0");

        return IPAddress;
    }
}

