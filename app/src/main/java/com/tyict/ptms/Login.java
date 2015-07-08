package com.tyict.ptms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tyict.ptms.dataInfo.DatabaseView;
import com.tyict.ptms.dataInfo.LoginControl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Login extends ActionBarActivity {

    private static boolean opened = false;
    private DatabaseView dbv = new DatabaseView();


    private Login getThis()
    {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(opened) {
            startActivity(NoStopable.i);
            this.finish();
        }
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btn_login_click(View v)
    {
        startActivity(new Intent().setClass(Login.this, A_Entry.class));
        finish();
        String userID = ((EditText)findViewById(R.id.et_loginID)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_password)).getText().toString();
        ((EditText)findViewById(R.id.et_loginID)).setEnabled(false);
        ((EditText)findViewById(R.id.et_password)).setEnabled(false);
        userID = "login1001";
        password = "pass1001";
        if(NoStopable.lc.login(userID,password))
        {
            NoStopable.i = new Intent(this, A_Entry.class);
            new getJobList().execute(LoginControl.getStaffID());
            NoStopable.opened = true;
        }
        else
        {
            ((TextView)findViewById(R.id.message)).setText("Login Failure, check your password and loginID");
            ((EditText)findViewById(R.id.et_loginID)).setEnabled(true);
            ((EditText)findViewById(R.id.et_password)).setEnabled(true);
        }
    }

    private class getJobList extends AsyncTask<String, Integer, String>
    {

        @Override
        protected void onPostExecute(String s) {
            startActivity(NoStopable.i);
        }

        @Override
        protected String doInBackground(String... staffNo) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request;

            try {
                String recvStr;
                String url = "http://itd-moodle.ddns.me/ptms/service_job.php?staffNo=" + staffNo[0];
                request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String t;
                recvStr = reader.readLine();
                while ((t = reader.readLine()) != null) {
                    recvStr += t;
                }

                reader.close();
                JSONArray json = new JSONObject(recvStr).getJSONArray("ServiceJob");
                for (int j = 0; j < json.length(); j++) {
                    JSONObject jo = json.getJSONObject(j);
                    String query = "INSERT INTO ServiceJob VALUES(" + "'" +
                            jo.getString("jobNo") + "','" +
                            jo.getString("requestDate") + "','" +
                            jo.getString("jobProblem") + "','" +
                            jo.getString("visitDate") + "','" +
                            jo.getString("jobStatus") + "','" +
                            jo.getString("jobStartTime") + "','" +
                            jo.getString("jobEndTime") + "','" +
                            jo.getString("serialNo") + "','" +
                            jo.getString("remark") +
                            "');";
                    dbv.exec(query);
                    getThis().finish();

                }
            } catch (IOException e) {

            } catch (JSONException e) {

            }
            return "Done!";
        }
    }

}
