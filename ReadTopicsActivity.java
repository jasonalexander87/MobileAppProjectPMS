package com.iasonas.melionis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ReadTopicsActivity extends AppCompatActivity {

    Button Next;
    Button Previous;
    Button CreateTopic;
    TextView tv;
    LinearLayout layout;
    String response;
    int PostsNumber;
    int CurrentRange = 0;
    String IP;
    int pagination;
    String cookie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readtopics);

        Intent Intent = getIntent();
        String forum = Intent.getStringExtra("forum");

        PostsNumber = Integer.parseInt(forum);

        pagination = getPagination();
        IP = getIP();
        cookie = getCookie();
        CurrentRange = pagination;

        CreateTopic = (Button)findViewById(R.id.buttonCreatTopic);
        Next = (Button)findViewById(R.id.buttonTopicsNext);
        Previous = (Button)findViewById(R.id.buttonTopicsPrevious);
        tv = (TextView) findViewById(R.id.textViewFTitle);
        layout = (LinearLayout) findViewById(R.id.LayoutTopics);

        new ReadTopicsTask().execute(IP, forum, String.valueOf(pagination), String.valueOf(CurrentRange), cookie);

        CreateTopic.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                Intent createTopic = new Intent(getApplicationContext(), CreateTopicActivity.class);
                createTopic.putExtra("forum", forum);
                startActivity(createTopic);

                finish();

            }
        });

        Next.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                if(CurrentRange < PostsNumber) {
                    CurrentRange = CurrentRange + pagination;
                    layout.removeAllViews();
                    new ReadTopicsTask().execute(IP, forum, String.valueOf(pagination), String.valueOf(CurrentRange), cookie);
                }


            }
        });

        Previous.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                if(CurrentRange- pagination > 0) {
                    CurrentRange = CurrentRange - pagination;
                    layout.removeAllViews();
                    new ReadTopicsTask().execute(IP ,forum , String.valueOf(pagination), String.valueOf(CurrentRange), cookie);
                }


            }
        });

    }

    private class ReadTopicsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result;
            Socket sock;
            PrintStream output;
            BufferedReader input;
            String request;
            String requestNumber;

                requestNumber = "getTopicsNum" + "/" + strings[1] + "/" + strings[4];
                request = "getTopics" + "/" + strings[1] + "/" + strings[2] + "/" + strings[3] + "/" + strings[4];

            try {
                InetAddress add = InetAddress.getByName(strings[0]);
                sock = new Socket(add, 9999);
                output = new PrintStream(sock.getOutputStream());
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                output.print(requestNumber);
                result = input.readLine();

                PostsNumber = Integer.valueOf(result);


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

        JSONObject topics;
        JSONArray topics2;
        int counter = 0;

        try {
            topics = new JSONObject(data);
            topics2 = topics.getJSONArray("topics");

            while(counter < topics2.length()) {
                JSONObject current = topics2.getJSONObject(counter);
                String title = current.getString("title");
                String location = current.getString("location");
                String description = current.getString("description");
                String userCreator = current.getString("creator");

                counter++;

                String text = "TITLE:" + title + "\n" + "LOCATION:" + location + "\n" + "DESCRIPTION:" + description+ "\n" + "CREATOR:" + userCreator;

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

                        Intent toTopics = new Intent(getApplicationContext(), ReadPostsActivity.class);
                        toTopics.putExtra("topic", title);

                        startActivity(toTopics);

                    }
                });
                layout.addView(tv);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getIP() {

        String IPAddress;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        IPAddress = sharedPrefs.getString("server", "0.0.0.0");

        return IPAddress;
    }

    public int getPagination() {

        int Pagination;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        Pagination = sharedPrefs.getInt("PGTN", 0);

        return Pagination;
    }

    public String getCookie() {

        String cookie;
        Context con = getApplicationContext();
        SharedPreferences sharedPrefs = con.getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        cookie = sharedPrefs.getString("COOKIE", "NULL");

        return cookie;
    }
}
