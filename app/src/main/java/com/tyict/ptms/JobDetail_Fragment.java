package com.tyict.ptms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.tyict.ptms.dataInfo.DatabaseView;


public class JobDetail_Fragment extends Fragment {
    View _this;
    private Bundle bundle;
    private String selectedjobNo;
    private TextView jobNo;
    private TextView jobProblem;
    private Spinner jobStatus;
    private String[] jobStatusItem = {"completed", "follow-up", "pending"};
    private TextView jobSerialNo;
    private TextView jobRequestDate;
    private TextView jobVisitDate;
    private TextView jobStartTime;
    private TextView jobEndTime;
    private TextView jobRemark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.jobdetail_layout, container, false);
        initVariable();
        setStatusItems();
        setjobDetail();
        return _this;
    }

    private void setStatusItems() {
        jobStatus.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, jobStatusItem));
    }

    private void initVariable() {
        bundle = getArguments();
        selectedjobNo = bundle.getString("selectedJobNo");
        jobNo = (TextView) _this.findViewById(R.id.jobDetail_jobNo);
        jobProblem = (TextView) _this.findViewById(R.id.jobDetail_problem);
        jobStatus = (Spinner) _this.findViewById(R.id.jobDetail_status);
        jobSerialNo = (TextView) _this.findViewById(R.id.jobDetail_serialNo);
        jobRequestDate = (TextView) _this.findViewById(R.id.jobDetail_requestDate);
        jobVisitDate = (TextView) _this.findViewById(R.id.jobDetail_visitDate);
        jobStartTime = (TextView) _this.findViewById(R.id.jobDetail_startTime);
        jobEndTime = (TextView) _this.findViewById(R.id.jobDetail_endTime);
        jobRemark = (TextView) _this.findViewById(R.id.jobDetail_remark);
    }


    public void setjobDetail() {
        Cursor cursor = DatabaseView.query("SELECT * FROM ServiceJob WHERE jobNo ='" + selectedjobNo + "'");
        cursor.moveToFirst();
        String _jobProblem = cursor.getString(cursor.getColumnIndex("jobProblem"));
        String _jobSerialNo = cursor.getString(cursor.getColumnIndex("serialNo"));
        String _jobRequestDate = cursor.getString(cursor.getColumnIndex("requestDate"));
        String _jobVisitDate = cursor.getString(cursor.getColumnIndex("visitDate"));
        String _jobStartTime = cursor.getString(cursor.getColumnIndex("jobStartTime"));
        String _jobEndTime = cursor.getString(cursor.getColumnIndex("jobEndTime"));
        String _jobRemark = cursor.getString(cursor.getColumnIndex("remark"));


        jobNo.setText(selectedjobNo);
        jobProblem.setText(_jobProblem);
        jobSerialNo.setText(_jobSerialNo);
        jobRequestDate.setText(_jobRequestDate);
        jobVisitDate.setText(_jobVisitDate);
        jobStartTime.setText(_jobStartTime);
        jobEndTime.setText(_jobEndTime);
        jobRemark.setText(_jobRemark);

        for (int i = 0; i < jobStatusItem.length; i++)
            if (jobStatusItem[i].equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("jobStatus"))))
                jobStatus.setSelection(i);

    }
}
