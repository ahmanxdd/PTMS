package com.tyict.ptms.dataInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.SyncStateContract;

import com.tyict.ptms.NoStopable;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmanxdd on 11/6/15.
 */

public class DatabaseView
{

    private static SQLiteDatabase db;
    private static boolean isinited = initDB();
    private static String[] table =
            {
                    "Technician", "Product", "Company", "Purchase", "ServiceJob"
            };


    public static String[] getAllTable()
    {
        return table;
    }

    public static Cursor query(String sql)
    {
        return query(sql, null);
    }

    public static Cursor query(String sql, String... params)
    {
        db = SQLiteDatabase.openDatabase("/data/data/com.tyict.ptms/database", null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery(sql, params);
    }

    public static void exec(String sql)
    {
        db = SQLiteDatabase.openDatabase("/data/data/com.tyict.ptms/database", null, SQLiteDatabase.OPEN_READWRITE);
        db.execSQL(sql);
    }

    private static boolean initDB()
    {

        db = SQLiteDatabase.openDatabase("/data/data/com.tyict.ptms/database", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        String sql = "DROP TABLE IF EXISTS Technician;";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS Product;";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS Company;";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS Purchase;";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS ServiceJob;";
        db.execSQL(sql);

        sql = "CREATE TABLE Technician (staffNo text PRIMARY KEY, staffLogin text, staffPswd text, staffName text);";
        db.execSQL(sql);

        sql = "CREATE TABLE Product(prodNo text PRIMARY KEY, prodName text, prodPrice double);";
        db.execSQL(sql);

        sql = "CREATE TABLE Company (comNo text PRIMARY KEY, comName text, comTel text, comAddr text);";
        db.execSQL(sql);

        sql = "CREATE TABLE Purchase(serialNo text PRIMARY KEY, purchaseDate date, prodNo text, comNo text);";
        db.execSQL(sql);

        sql = "CREATE TABLE ServiceJob(jobNo text PRIMARY KEY, requestDate date, jobProblem text," +
                " visitDate date, jobStatus text, jobStartTime text, jobEndTime text, serialNo text, remark text);";
        db.execSQL(sql);


        sql = "INSERT INTO Technician VALUES" +
                "('1001', 'login1001', 'pass1001', 'Jacky Wong')," +
                "('1002', 'login1002', 'pass1002', 'Kevin Leung')," +
                "('1003', 'login1003', 'pass1003', 'Flora Chan')," +
                "('1004', 'login1004', 'pass1004', 'Alan Lam');";
        db.execSQL(sql);

        sql = "INSERT INTO Product VALUES" +
                "('CN1008', 'CanonPowerPhotocopier', 4889)," +
                "('CN2186', 'Canon Inket Printer', 1635)," +
                "('HP1022', 'HP LaserJet 1022 Printer', 2500)," +
                "('HP2055', 'HP LaserJet Colour Printer', 3500);";
        db.execSQL(sql);

        sql = "INSERT INTO Company VALUES" +
                "('2001', 'Royal Pacific Hotel', '27368818', 'Royal Pacific Hotel & Towers, China Hong Kong City, 33 Canton Road, Tsim Sha Tsui')," +
                "('2002', 'Hang Seng Bank Ltd', '28220228', '83 Des Voeux Rd C, Central District')," +
                "('2003', 'American Express Bank Ltd', '22771010', 'One Exchange Square, Central District');";
        db.execSQL(sql);

        sql = "INSERT INTO Purchase VALUES" +
                "('34738783298', '20/4/2014', 'HP1022', '2003')," +
                "('38473878893', '15/8/2013', 'CN2186', '2002')," +
                "('42389489993', '18/12/2014', 'CN1008', '2003')," +
                "('48947347894', '21/2/2012', 'HP1022', '2002')," +
                "('89347827894', '17/1/2013', 'HP2055', '2002')," +
                "('46917347228', '17/1/2013', 'HP1022', '2001');";
        db.execSQL(sql);

        sql = "INSERT INTO ServiceJob VALUES" +
                "('501', '15/5/2015', 'Toner is not securely fused on the printout', '28/5/2015', 'completed', '10:05', '10:43', '34738783298', 'sensor S3001 to be replaced - must bring in next visit')," +
                "('502', '14/5/2015', 'Print image is not sharp and paper feeder always jam', '16/5/2015', 'follow-up', '14:20', '15:31', '42389489993', 'sensor S3001 to be replaced - must bring in next visit')," +
                "('503', '17/5/2015', 'Paper jam in tray 1', null, 'pending', null, null, '34738783298', 'sensor S3001 to be replaced - must bring in next visit')," +
                "('504', '23/5/2015', 'Paper feeder can only feed 1 page', null, 'pending', null, null, '42389489993', null)," +
                "('505', '12/5/2015', 'The printout is very light in colour even after we use new ink catridges', null, 'pending', null, null, '89347827894', null)," +
                "('506', '22/5/2015', 'The printer cannot print pages with complex graphics', null, 'pending', null, null, '48947347894', null)";
        db.execSQL(sql);
        return true;
    }


    public static AsyncTask<String, Integer, String> getJobsHandler()
    {
        return new AsyncTask<String, Integer, String>()
        {

            @Override
            protected String doInBackground(String... staffNo)
            {

                HttpClient client = new DefaultHttpClient();
                HttpGet request;

                try
                {
                    String recvStr;
                    String url = "http://itd-moodle.ddns.me/ptms/service_job.php?staffNo=" + staffNo[0];
                    request = new HttpGet(url);
                    HttpResponse response = client.execute(request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String t;
                    recvStr = reader.readLine();
                    while ((t = reader.readLine()) != null)
                    {
                        recvStr += t;
                    }
                    reader.close();
                    String sql = "DROP TABLE IF EXISTS ServiceJob;";
                    //  dbv.exec(sql);
                    sql = "CREATE TABLE ServiceJob(jobNo text PRIMARY KEY, requestDate date, jobProblem text," +
                            " visitDate date, jobStatus text, jobStartTime text, jobEndTime text, serialNo text, remark text);";
                    //         dbv.exec(sql);
                    JSONArray json = new JSONObject(recvStr).getJSONArray("ServiceJob");
                    for (int j = 0; j < json.length(); j++)
                    {
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

                    }
                } catch (IOException e)
                {

                } catch (JSONException e)
                {

                }
                return "Done!";
            }
        };
    }


}