package com.tyict.ptms;

import android.app.Activity;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.tyict.ptms.dataInfo.DatabaseView;

public class Joblist_activity extends Fragment {
    View rootView;
    ListView lvTable;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.joblist_layout, container, false);
        return rootView;
    }

}
