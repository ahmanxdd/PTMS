package com.tyict.ptms;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class Add_ServiceJob_Fragment extends Fragment implements View.OnClickListener, TextWatcher {
    View _this;

    private TextView jobNo;
    private TextView requestDate;
    private EditText serialNo;
    private TextView prodNo;
    private TextView comName;
    private TextView errorMsg;
    private EditText problem;
    private EditText remark;
    private Button btnNewServiceJob;
    private Button btnFind;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.add_servicejob_layout, container, false);
        initVariable();
        return _this;
    }

    protected void initVariable() {
        jobNo = (TextView) _this.findViewById(R.id.newServiceJob_jobNo);
        requestDate = (TextView) _this.findViewById(R.id.newServiceJob_requestDate);
        serialNo = (EditText) _this.findViewById(R.id.newServiceJob_serialNo);
        prodNo = (TextView) _this.findViewById(R.id.newServiceJob_prodNo);
        comName = (TextView) _this.findViewById(R.id.newServiceJob_comName);
        problem = (EditText) _this.findViewById(R.id.newServiceJob_problem);
        remark = (EditText) _this.findViewById(R.id.newServiceJob_remark);
        errorMsg = (TextView) _this.findViewById(R.id.newServicJob_errorMsg);
        btnNewServiceJob = (Button) _this.findViewById(R.id.newServiceJob_btnNewServiceJob);
        btnFind = (Button) _this.findViewById(R.id.newServiceJob_btnFind);

        btnFind.setOnClickListener(this);
        btnNewServiceJob.setOnClickListener(this);
        serialNo.addTextChangedListener(this);


        Cursor cursor = DatabaseView.query("SELECT jobNO FROM ServiceJob ORDER BY jobNo DESC");
        cursor.moveToNext();
        jobNo.setText((Integer.parseInt(cursor.getString(cursor.getColumnIndex("jobNo"))) + 1) + "");

        String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        requestDate.setText(date);

    }


    @Override
    public void onClick(View v) {
        if (v.equals(btnNewServiceJob)){
            ;
        }else if (v.equals(btnFind)){
            ;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(serialNo.getText().toString().length() == 0){
            errorMsg.setVisibility(View.INVISIBLE);
        }
        else{
            Cursor cursor = DatabaseView.query("SELECT c.comName, pt.prodNo FROM Product pt, Purchase pu, Company c WHERE "
            + "pu.prodNo = pt.prodNo AND "
            + "pu.comNo = c.comNo AND "
            + "pu.serialNo = '" + serialNo.getText().toString().trim() + "'");
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                if (serialNo.getText().toString() == cursor.getString(cursor.getColumnIndex("serialNo")))
                    errorMsg.setVisibility(View.INVISIBLE);
            }
            else
                errorMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
