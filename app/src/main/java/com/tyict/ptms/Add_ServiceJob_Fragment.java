package com.tyict.ptms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private boolean isValid = false;

    private Spinner sComName;
    private Spinner sProdName;
    private String selectedComName;
    private String selectedProdName;
    private Button btnSubmit;
    private String[] comNameArray;
    private String[] prodNameArray;

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
        btnFind.setOnClickListener(this);
        serialNo.addTextChangedListener(this);


        Cursor cursor = DatabaseView.query("SELECT jobNO FROM ServiceJob ORDER BY jobNo DESC");
        cursor.moveToNext();
        jobNo.setText((Integer.parseInt(cursor.getString(cursor.getColumnIndex("jobNo"))) + 1) + "");

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        requestDate.setText(date);

    }


    @Override
    public void onClick(View v) {
        if (v.equals(btnNewServiceJob)) {
            insertTodatabase();
        } else if (v.equals(btnFind)) {
            showFindSerialNoDialog();
        }
    }

    private void setSerialNo() {
        Cursor cursor = DatabaseView.query("SELECT serialNo FROM Purchase pu, Product pt, Company c WHERE "
                + " pu.prodNo =  pt.prodNo AND "
                + " pu.comNo = c.comNo AND "
                + " c.comName = '" + selectedComName + "' AND"
                + " pt.prodName = '" + selectedProdName + "'");
        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Cannot find!", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToNext();
            serialNo.setText(cursor.getString(cursor.getColumnIndex("serialNo")));
        }
    }

    private void showFindSerialNoDialog() {

        Cursor cursor = DatabaseView.query("SELECT comName FROM Company");
        comNameArray = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            comNameArray[i] = cursor.getString(cursor.getColumnIndex("comName"));
        }

        Cursor cursor2 = DatabaseView.query("SELECT prodName FROM Product");
        prodNameArray = new String[cursor2.getCount()];
        for (int i = 0; i < cursor2.getCount(); i++) {
            cursor2.moveToNext();
            prodNameArray[i] = cursor2.getString(cursor2.getColumnIndex("prodName"));
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.find_serialno_dialog_layout, null);
        sComName = (Spinner) dialog.findViewById(R.id.findDialog_comName);
        sProdName = (Spinner) dialog.findViewById(R.id.findDialog_prodName);
        btnSubmit = (Button) dialog.findViewById(R.id.findDialog_submit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedComName = (String) sComName.getSelectedItem();
                        selectedProdName = (String) sProdName.getSelectedItem();
                        Toast.makeText(getActivity(), selectedComName + " " + selectedProdName, Toast.LENGTH_SHORT).show();
                        setSerialNo();
                    }
                }
        );
        ArrayAdapter aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, comNameArray);
        sComName.setAdapter(aa);
        ArrayAdapter bb = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, prodNameArray);
        sProdName.setAdapter(bb);

        builder.setView(dialog);
        builder.show();

    }

    private void insertTodatabase() {
        if (isValid) {
            DatabaseView.exec("INSERT INTO ServiceJob(jobNo, requestDate, jobProblem, jobStatus, serialNo, remark) VALUES ('"
                    + jobNo.getText().toString() + "', '"
                    + requestDate.getText().toString() + "', '"
                    + problem.getText().toString() + "', '"
                    + "pending" + "', '"
                    + serialNo.getText().toString() + "', '"
                    + remark.getText().toString() + "')");

            Toast.makeText(getActivity(), "Successful insert new service job!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Invalid serial no!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (serialNo.getText().toString().length() == 0) {
            errorMsg.setVisibility(View.INVISIBLE);
        } else {
            Cursor cursor = DatabaseView.query("SELECT serialNo, c.comName, pt.prodNo FROM Product pt, Purchase pu, Company c WHERE "
                    + "pu.prodNo = pt.prodNo AND "
                    + "pu.comNo = c.comNo AND "
                    + "pu.serialNo = '" + serialNo.getText().toString().trim() + "'");
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                if (serialNo.getText().toString().equals(cursor.getString(cursor.getColumnIndex("serialNo")))) {
                    errorMsg.setVisibility(View.INVISIBLE);
                    prodNo.setText(cursor.getString(cursor.getColumnIndex("prodNo")));
                    comName.setText(cursor.getString(cursor.getColumnIndex("comName")));
                    isValid = true;

                }
            } else {
                errorMsg.setVisibility(View.VISIBLE);
                prodNo.setText("");
                comName.setText("");
                isValid = false;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
