package com.tyict.ptms.dataInfo;

import android.database.Cursor;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by RAYMOND on 7/5/2015.
 */
public class StaticInfo {

    private static TreeMap<String,String> _pairs = new TreeMap<String, String>() {
    };
    private static Cursor c;

    public static TreeMap getAllProduct()
    {
        clearAll();
        c = DatabaseView.query("SELECT * FROM Product");
        while(c.moveToNext())
        {
            _pairs.put(c.getString(c.getColumnIndex("prodName")), c.getString(c.getColumnIndex("prodNo")));
        }
        return _pairs;
    }

    public static TreeMap getAllCompany() {
        clearAll();
        c = DatabaseView.query("SELECT * FROM Company");
        while (c.moveToNext()) {
            _pairs.put(c.getString(c.getColumnIndex("comName")), c.getString(c.getColumnIndex("comNo")));
        }
        return _pairs;
    }

    private static void clearAll() {
        _pairs.clear();
    }

}
