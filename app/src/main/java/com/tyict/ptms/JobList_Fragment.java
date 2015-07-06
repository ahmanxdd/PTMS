package com.tyict.ptms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.ArrayList;

public class JobList_Fragment extends Fragment {

    View _this;
    private ListView lvJobList;
    private Context context;
    private ArrayList<ListData> myList = new ArrayList<>();
    private String defaultListViewItemsSQL = "SELECT * FROM ServiceJob";
    private String[] jobNo;
    private String[] problems;
    private String[] status;
    private String[] datetime;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.joblist_layout, container, false);
        initVariable();
        initJobListItems();
        return _this;
    }

    private void setJobListItems(Cursor cursor) {
        fillDetail(cursor);
        getDetailInList();
        lvJobList.setAdapter(new MyBaseAdapter(context, myList));
    }

    private void fillDetail(Cursor cursor) {
        jobNo = new String[cursor.getCount()];
        problems = new String[cursor.getCount()];
        status = new String[cursor.getCount()];
        datetime = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            jobNo[i] = "Job No:" + cursor.getString(cursor.getColumnIndex("jobNo"));
            problems[i] = cursor.getString(cursor.getColumnIndex("jobProblem"));
            status[i] = cursor.getString(cursor.getColumnIndex("jobStatus"));
            datetime[i] = cursor.getString(cursor.getColumnIndex("visitDate")) + " " +
                    cursor.getString(cursor.getColumnIndex("jobStartTime"));
        }
    }

    private void getDetailInList() {
        myList.clear();
        for (int i = 0; i < jobNo.length; i++) {
            ListData ld = new ListData();
            ld.setId(jobNo[i]);
            ld.setProblem(problems[i]);
            ld.setStatus(status[i]);
            ld.setDatatime(datetime[i]);
            myList.add(ld);
        }
    }

    private void initVariable() {
        lvJobList = (ListView) _this.findViewById(R.id.lvJobList);
        context = _this.getContext();
    }

    private void initJobListItems() {
        fillDetail(DatabaseView.query(defaultListViewItemsSQL));
        getDetailInList();
        lvJobList.setAdapter(new MyBaseAdapter(context, myList));
    }

}
