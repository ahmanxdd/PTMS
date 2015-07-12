package com.tyict.ptms;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


    private Login getThis() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NoStopable.lc.isLogin()) {
            startActivity(NoStopable.i);
            this.finish();
        }


        setContentView(R.layout.activity_login);
    }


    public void btn_login_click(View v) {
        String userID = ((EditText) findViewById(R.id.et_loginID)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
        userID = "login1001";
        password = "pass1001";
        findViewById(R.id.et_loginID).setEnabled(false);
        findViewById(R.id.et_password).setEnabled(false);
        if (NoStopable.lc.login(userID, password)) {
            NoStopable.i = new Intent(this, A_Entry.class);
            new getJobList().execute(LoginControl.getStaffID());
            NoStopable.opened = true;
        } else {
            ((TextView) findViewById(R.id.message)).setText("Login Failure, check your password and loginID");
            ((EditText) findViewById(R.id.et_loginID)).setEnabled(true);
            ((EditText) findViewById(R.id.et_password)).setEnabled(true);
        }
    }

    private class getJobList extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            getThis().finish();
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
                String sql = "DROP TABLE IF EXISTS ServiceJob;";
                //  dbv.exec(sql);
                sql = "CREATE TABLE ServiceJob(jobNo text PRIMARY KEY, requestDate date, jobProblem text," +
                        " visitDate date, jobStatus text, jobStartTime text, jobEndTime text, serialNo text, remark text);";
                //         dbv.exec(sql);
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
                    //      dbv.exec(query);
                    getThis().finish();

                }
            } catch (IOException e) {

            } catch (JSONException e) {

            }
            return "Done!";
        }
    }

}