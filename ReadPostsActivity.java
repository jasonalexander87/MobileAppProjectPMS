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

public class ReadPostsActivity extends AppCompatActivity {

    Button Next;
    Button Previous;
    Button CreatePost;
    TextView tv;
    LinearLayout layout;
    String cookie;
    int PostsNumber;
    int CurrentRange;
    String IP;
    int pagination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readposts);

        Intent Intent = getIntent();
        String topic = Intent.getStringExtra("topic");

        PostsNumber = Integer.parseInt(topic);

        pagination = getPagination();
        IP = getIP();
        cookie = getCookie();
        CurrentRange = pagination;

        CreatePost = (Button)findViewById(R.id.buttonCreatePost);
        Next = (Button)findViewById(R.id.buttonPostsNext);
        Previous = (Button)findViewById(R.id.buttonPostsPrevious);
        tv = (TextView) findViewById(R.id.textViewPosts);
        layout = (LinearLayout) findViewById(R.id.LayoutPosts);

        new ReadPostsTask().execute(IP ,topic , String.valueOf(pagination), String.valueOf(CurrentRange), cookie);

        CreatePost.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                Intent createPost = new Intent(getApplicationContext(), CreatePostActivity.class);
                createPost.putExtra("topic", topic);
                startActivity(createPost);

                finish();

            }
        });



        Next.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                if(CurrentRange < PostsNumber) {
                    CurrentRange = CurrentRange + pagination;
                    new ReadPostsTask().execute(IP ,topic , String.valueOf(pagination), String.valueOf(CurrentRange), cookie);
                }


            }
        });

        Previous.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View _view) {

                if(CurrentRange- pagination < PostsNumber) {
                    CurrentRange = CurrentRange - pagination;
                    layout.removeAllViews();
                    new ReadPostsTask().execute(IP ,topic , String.valueOf(pagination), String.valueOf(CurrentRange), cookie);
                }


            }
        });

    }

    private class ReadPostsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result;
            Socket sock;
            PrintStream output;
            BufferedReader input;
            String request;
            String requestNumber;

            requestNumber = "getPostsNum" + "/" + strings[4];
            request = "getPosts" + "/" + strings[1] + "/" + strings[2] + "/" + strings[3] + "/" + strings[4];

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

            updateView2(s);
        }


    }

    public void updateView2(String data) {

        JSONObject forums;
        JSONArray forums2;
        int counter = 0;

        try {
            forums = new JSONObject(data);
            forums2 = forums.getJSONArray("posts");

            while(counter < forums2.length()) {
                JSONObject current = forums2.getJSONObject(counter);
                String title = current.getString("title");
                String location = current.getString("location");
                String description = current.getString("description");
                String userCreator = current.getString("creator");

                counter++;

                String text = "TITLE:" + title + "\n" + "LOCATION:" + location + "\n" + "DESCRIPTION:" + description+ "\n" + "CREATOR:" + userCreator;

                TextView tv = new TextView(this);
                tv.setText(title);
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
                layout.addView(tv);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateView(String data) {

        JSONObject topics;

        try {
            topics = new JSONObject(data);

            Iterator<String> iter = topics.keys();
            while(iter.hasNext()) {
                String key = iter.next();
                JSONArray value =  topics.getJSONArray(key);
                String title = value.getString(0);
                String location = value.getString(3);

                TextView tv = new TextView(this);
                tv.setText(title);
                tv.setClickable(true);
                tv.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View _view) {

                        Intent toPosts = new Intent(getApplicationContext(), ReadPostsActivity.class);
                        toPosts.putExtra("location", location);

                        startActivity(toPosts);

                    }
                });
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent tomap = new Intent(getApplicationContext(), MapActivity.class);
                        tomap.putExtra("title", title);

                        startActivity(tomap);

                        return false;
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

