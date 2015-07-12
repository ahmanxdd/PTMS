package com.tyict.ptms.JobService;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.A_Entry;
import com.tyict.ptms.Adapter.MyBaseAdapter;
import com.tyict.ptms.Adapter.MySwappableAdapter;
import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.ArrayList;

public class JobList_Fragment extends Fragment
{

    View _this;
    private ListView lvJobList;
    private Context context;
    private String defaultListViewItemsSQL;
    private String[] jobNo;
    private String[] problems;
    private String[] status;
    private String[] datetime;
    private String _status;
    RefreshableView refreshableView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        defaultListViewItemsSQL = "SELECT * FROM ServiceJob";

        try
        {
            _status = getArguments().getString("status");
            defaultListViewItemsSQL += " WHERE jobStatus = '" + _status + "'";
        } catch (Exception e)
        {
            _status = "none";
        }

        _this = inflater.inflate(R.layout.f_job_list_layout, container, false);
        initVariable();
        initJobListItems();
        refreshableView = (RefreshableView) _this.findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    Thread.sleep(3000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
        lvJobList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedJobNo = ((TextView) view.findViewById(R.id.tvJobNo)).getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("selectedJobNo", selectedJobNo);
                Fragment jobDetail_Fragment = new F_JobDetail();
                jobDetail_Fragment.setArguments(bundle);
                ((A_Entry) getActivity()).transferTo(jobDetail_Fragment, true);
                Toast.makeText(getActivity(), selectedJobNo, Toast.LENGTH_SHORT).show();
            }
        });


        return _this;
    }


    public void fillDetail()
    {
        Cursor cursor = DatabaseView.query(defaultListViewItemsSQL);
        jobNo = new String[cursor.getCount()];
        problems = new String[cursor.getCount()];
        status = new String[cursor.getCount()];
        datetime = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            jobNo[i] = cursor.getString(cursor.getColumnIndex("jobNo"));
            problems[i] = cursor.getString(cursor.getColumnIndex("jobProblem"));
            status[i] = cursor.getString(cursor.getColumnIndex("jobStatus"));

            if (cursor.getString(cursor.getColumnIndex("visitDate")) == null || cursor.getString(cursor.getColumnIndex("visitDate")).equals(""))
                datetime[i] = "Not assigned ";
            else
            {
                datetime[i] = cursor.getString(cursor.getColumnIndex("visitDate")) + " " +
                        cursor.getString(cursor.getColumnIndex("jobStartTime"));
            }
        }
    }

    private ArrayList getDetailInList()
    {
        ArrayList<ListData> myList = new ArrayList<>();
        for (int i = 0; i < jobNo.length; i++)
        {
            ListData ld = new ListData();
            ld.setId(jobNo[i]);
            ld.setProblem(problems[i]);
            ld.setStatus(status[i]);
            if (datetime[i] == "")
                ld.setDatatime(null);
            else
                ld.setDatatime(datetime[i]);
            myList.add(ld);
        }
        return myList;
    }

    private void initVariable()
    {
        lvJobList = (ListView) _this.findViewById(R.id.lvJobList);

        context = _this.getContext();
    }

    private void initJobListItems()
    {
        fillDetail();
        if (_status.equals("pending"))
            lvJobList.setAdapter(new MySwappableAdapter(context, getDetailInList()));
        else
            lvJobList.setAdapter(new MyBaseAdapter(context, getDetailInList()));
    }


}
