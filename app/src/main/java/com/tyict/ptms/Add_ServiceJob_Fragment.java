package com.tyict.ptms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.Calendar;

public class Add_ServiceJob_Fragment extends Fragment implements View.OnClickListener {
    View _this;

    private TextView jobNo;
    private TextView requestDate;
    private TextView prodNo;
    private TextView prodName;
    private TextView purshaseDate;
    private TextView price;
    private TextView problem;
    private TextView comName;
    private TextView comTel;
    private TextView comAddress;
    private TextView remark;
    private Button btnNewServiceJob;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.add_servicejob_layout, container, false);
        initVariable();
        btnNewServiceJob.setOnClickListener(this);
        return _this;
    }

    private void initVariable() {
        jobNo = (TextView) _this.findViewById(R.id.newServiceJob_jobNo);
        requestDate = (TextView) _this.findViewById(R.id.newServiceJob_requestDate);
        prodNo = (TextView) _this.findViewById(R.id.newServiceJob_prodNo);
        prodName = (TextView) _this.findViewById(R.id.newServiceJob_prodName);
        purshaseDate = (TextView) _this.findViewById(R.id.newServiceJob_purchaseDate);
        price = (TextView) _this.findViewById(R.id.newServiceJob_price);
        problem = (TextView) _this.findViewById(R.id.newServiceJob_problem);
        comName = (TextView) _this.findViewById(R.id.newServiceJob_comName);
        comTel = (TextView) _this.findViewById(R.id.newServiceJob_comTel);
        comAddress = (TextView) _this.findViewById(R.id.newServiceJob_comAddress);
        remark = (TextView) _this.findViewById(R.id.newServiceJob_remark);
        btnNewServiceJob = (Button) _this.findViewById(R.id.newServiceJob_btnNewServiceJob);

        Cursor cursor = DatabaseView.query("SELECT * FROM ServiceJob ORDER BY jobNo");
        cursor.moveToLast();
        jobNo.setText(Integer.toString(Integer.parseInt(cursor.getString(cursor.getColumnIndex("jobNo"))) + 1));

        Calendar c = Calendar.getInstance();
        requestDate.setText(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DATE));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newServiceJob_btnNewServiceJob:
                if (!checkDataFinish())
                    Toast.makeText(getActivity(), "Missing enter data", Toast.LENGTH_SHORT).show();
                else {
                    insertToDatabase();
                    Toast.makeText(getActivity(), "Successful new service job!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void insertToDatabase() {
        String comNo = "";
        String serialNo = "";
        String jobStatus = "pending";
        DatabaseView.query("INSERT INTO ServiceJob(jobNO, requestDate, jobProblem, jobStatus, serialNo, remark) VALUES ('"
                + jobNo.getText().toString() + "', '"
                + requestDate.getText().toString() + "', '"
                + problem.getText().toString() + "', '"
                + jobStatus + "', '"
                + serialNo + "', '"
                + remark.getText().toString() + "')");


        DatabaseView.query("INSERT INTO Purchase VALUES ('"
                + serialNo + "', '"
                + purshaseDate.getText().toString() + "', '"
                + prodNo.getText().toString() + "', '"
                + comNo + "')");

        DatabaseView.query("INSERT INTO Product VALUES ('"
                + prodNo.getText().toString() + "', '"
                + prodName.getText().toString() + "', '"
                + price.getText().toString() + "')");

    }

    private boolean checkDataFinish() {
        if (prodNo.getText().toString().trim().length() == 0) {
            prodNo.requestFocus();
            return false;
        } else if (prodName.getText().toString().trim().length() == 0) {
            prodName.requestFocus();
            return false;
        } else if (purshaseDate.getText().toString().trim().length() == 0) {
            purshaseDate.requestFocus();
            return false;
        } else if (price.getText().toString().trim().length() == 0) {
            price.requestFocus();
            return false;
        } else if (problem.getText().toString().trim().length() == 0) {
            problem.requestFocus();
            return false;
        } else if (comName.getText().toString().trim().length() == 0) {
            comName.requestFocus();
            return false;
        } else if (comTel.getText().toString().trim().length() == 0) {
            comTel.requestFocus();
            return false;
        } else if (comAddress.getText().toString().trim().length() == 0) {
            comAddress.requestFocus();
            return false;
        }
        return true;
    }
}
