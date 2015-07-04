package com.tyict.ptms.dataInfo;

import android.database.Cursor;

import java.util.HashMap;

/**
 * Created by RAYMOND on 7/5/2015.
 */
public class StaticInfo {
    private static DatabaseView dbv = new DatabaseView();
    private static HashMap<String,String> _pairs = new HashMap<String, String>() {
    };
    private static Cursor c;

    public static HashMap getAllProduct()
    {
        clearAll();
        c = dbv.query("SELECT * FROM Product");
        while(c.moveToNext())
        {
            _pairs.put(c.getString(c.getColumnIndex("prodName")), c.getString(c.getColumnIndex("prodNo")));
        }
        return _pairs;
    }

    public static HashMap getAllCompany() {
        clearAll();
        c = dbv.query("SELECT * FROM Company");
        while (c.moveToNext()) {
            _pairs.put(c.getString(c.getColumnIndex("comName")), c.getString(c.getColumnIndex("comNo")));
        }
        return _pairs;
    }

    private static void clearAll() {
        _pairs.clear();
    }

}
