package com.tyict.ptms;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tyict.ptms.dataInfo.DatabaseView;
import com.tyict.ptms.dataInfo.StaticInfo;

import java.util.HashMap;

/**
 * Created by RAYMOND on 7/5/2015.
 */
public class F_productIssues extends Fragment{
    @Nullable
    private DatabaseView dbv;
    private HashMap<String, String> _products;
    private View _this;
    private TextView tv_job, tv_date, tv_problem;
    private Spinner productSelection;
    private ListView jobList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.f_product_issues, container, false);
        dbv = new DatabaseView();
        findView();
        _products = StaticInfo.getAllProduct();
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, _products.keySet().toArray(new String[_products.size()]));
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSelection.setAdapter(aa);
        productSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (jobList == null) return;
                Cursor c = dbv.query("SELECT ServiceJob.jobNo FROM ServiceJob, Purchase, Product " +
                        "WHERE Product.prodNo = Purchase.prodNo AND Purchase.serialNo = ServiceJob.serialNo" +
                        " AND Product.prodNo = '" + _products.get(productSelection.getSelectedItem().toString()) + "'");
                String[] list = new String[c.getCount()];
                for (int j = 0; j < c.getCount(); j++) {
                    c.moveToNext();
                    list[j] = c.getString(0);
                }
                ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                jobList.setAdapter(aa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        jobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String jobNo = jobList.getItemAtPosition(i).toString();
                Cursor c = dbv.query("SELECT * FROM ServiceJob WHERE jobNo = '" + jobNo + "'");
                c.moveToNext();
                tv_problem.setText(c.getString(c.getColumnIndex("jobProblem")));
                tv_job.setText(c.getString(c.getColumnIndex("jobNo")));
                tv_date.setText(c.getString(c.getColumnIndex("requestDate")));
            }
        });

        return _this;
    }
    private void findView()
    {
        tv_job = (TextView)_this.findViewById(R.id.tv_productIssues_jobNo);
        tv_date = (TextView)_this.findViewById(R.id.tv_productIssues_date);
        tv_problem = (TextView)_this.findViewById(R.id.tv_productIssues_problem);
        productSelection = (Spinner)_this.findViewById(R.id.spn_productSelection);
        jobList = (ListView)_this.findViewById(R.id.productIssues_lv_jobNo);
    }

}
