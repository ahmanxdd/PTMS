package com.tyict.ptms.dataInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.SyncStateContract;

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

public class DatabaseView {

    private static SQLiteDatabase db;
    private static boolean isinited = initDB();
    private static String[] table =
            {
                    "Technician", "Product", "Company", "Purchase", "ServiceJob"
            };
    private static String[] table_format =
            {
                    "%4s %12s %14s %-10s\n", "%6s %-20s %.2f\n", "%4s %s %8s %50s\n", "%11s %10s %6s %4s\n", "%3s"
            };



    public static Cursor getAllCompany() {
        Cursor c = query("SELECT * FROM Company");
        return  c;
    }
    public static String[] getTableFormat()
    {
        return table_format;
    }
    public static String[] getAllTable()  {
/*        Cursor c = query("SELECT name FROM sqlite_master WHERE type='table'", null);
        String[] retStr = new String[c.getCount() - 1];
        int i = 0;
        c.moveToNext();
        while (c.moveToNext())
        {
            retStr[i++] = c.getString(c.getColumnIndex("name"));
        }
        return  retStr;*/
        return table;
    }
    public static Cursor query(String sql) {
        return query(sql,null);
    }
    public static Cursor query(String sql, String... params) {
        db = SQLiteDatabase.openDatabase("/data/data/com.tyict.ptms/database", null, SQLiteDatabase.OPEN_READONLY);
        return db.rawQuery(sql, params);
    }
    public static void exec(String sql) {
        db = SQLiteDatabase.openDatabase("/data/data/com.tyict.ptms/database", null, SQLiteDatabase.OPEN_READWRITE);
        db.execSQL(sql);
        db.close();
    }
    private static boolean initDB() {

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
                "('34638783298', '20/4/2014', 'HP1022', '2003')," +
                "('38473878893', '15/8/2013', 'CN2186', '2002')," +
                "('42389489993', '18/12/2014', 'CN1008', '2003')," +
                "('48947347894', '21/2/2012', 'HP1022', '2002')," +
                "('89347827894', '17/1/2013', 'HP2055', '2002')," +
                "('46917347228', '17/1/2013', 'HP1022', '2001');";
        db.execSQL(sql);
        return true;
    }
}