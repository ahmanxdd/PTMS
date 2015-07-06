package com.tyict.ptms;

import android.content.Context;
import android.database.Cursor;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.ArrayList;

public class Joblist_activity extends Fragment {
    View rootView;
    static ListView lvJobList;
    static Context context;
    static ArrayList<ListData> myList = new ArrayList<>();
    static String listViewSQL = "SELECT * FROM ServiceJob";
    static String[] ids;
    static String[] problems;
    static String[] status;
    static String[] datatime;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.joblist_layout, container, false);
        context = container.getContext();
        fillDetail() ;

        lvJobList = (ListView) rootView.findViewById(R.id.lvJobList);
        getDataInList();
        Toast.makeText(getActivity(), "HEY", Toast.LENGTH_SHORT).show();
        lvJobList.setAdapter(new MyBaseAdapter(context, myList));
        lvJobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public static void getDataInList() {
        myList.clear();
        for (int i = 0; i < ids.length; i++) {
            ListData ld = new ListData();
            ld.setId(ids[i]);
            ld.setProblem(problems[i]);
            ld.setStatus(status[i]);
            ld.setDatatime(datatime[i]);
            myList.add(ld);
        }
    }

    public static void fillDetail() {
        Cursor cursor = DatabaseView.query(listViewSQL);

        ids = new String[cursor.getCount()];
        problems = new String[cursor.getCount()];
        status = new String[cursor.getCount()];
        datatime = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            ids[i] = "Job No:" + cursor.getString(cursor.getColumnIndex("jobNo"));
            problems[i] = cursor.getString(cursor.getColumnIndex("jobProblem"));
            status[i] = cursor.getString(cursor.getColumnIndex("jobStatus"));
            datatime[i] = cursor.getString(cursor.getColumnIndex("visitDate")) + " " +
                    cursor.getString(cursor.getColumnIndex("jobStartTime"));
        }
    }

}
