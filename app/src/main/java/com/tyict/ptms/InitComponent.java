package com.tyict.ptms;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tyict.ptms.dataInfo.DatabaseView;

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


public class InitComponent extends ActionBarActivity {

    private DatabaseView dbv = new DatabaseView();
    private Intent i;
    private InitComponent getThis()
    {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = new Intent(getThis(), RaymondTest.class);
        setContentView(R.layout.activity_init_component);
        new getJobList().execute("1001");
        finish();
    }
    private class getJobList extends AsyncTask<String, Integer, String>
    {

        @Override
        protected void onPostExecute(String s) {
            startActivity(i);
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
                }
            } catch (IOException e) {

            } catch (JSONException e) {

            }
            return "Done!";
        }
    }

}
