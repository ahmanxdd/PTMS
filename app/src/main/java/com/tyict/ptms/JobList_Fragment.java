package com.tyict.ptms;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.JobService.ListData;
import com.tyict.ptms.JobService.MyBaseAdapter;
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
    RefreshableView refreshableView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.joblist_layout, container, false);
        initVariable();
        initJobListItems();
        refreshableView = (RefreshableView) _this.findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
        lvJobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobNo = ((TextView) view.findViewById(R.id.tvJobNo)).getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("selectedJobNo", selectedJobNo);
                Fragment jobDetail_Fragment = new JobDetail_Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.mainContent, jobDetail_Fragment);
                jobDetail_Fragment.setArguments(bundle);
                transaction.commit();
                Toast.makeText(getActivity(), selectedJobNo, Toast.LENGTH_SHORT).show();
            }
        });

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
            jobNo[i] = cursor.getString(cursor.getColumnIndex("jobNo"));
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
