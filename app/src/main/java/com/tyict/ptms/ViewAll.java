package com.tyict.ptms;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.TextView;

import com.tyict.ptms.dataInfo.DatabaseView;



/**
 * Created by RAYMOND on 7/4/2015.
 */
public class ViewAll extends Fragment{

    Spinner spn_tableSelection;
    String[] table;
    String[] table_format;
    DatabaseView dv;
    TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dv = new DatabaseView();
        table =  dv.getAllTable();
        table_format = dv.getTableFormat();
        View view = inflater.inflate(R.layout.fragment_view_all, container, false);
        spn_tableSelection = (Spinner)view.findViewById(R.id.spn_tableSelection);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, table);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_tableSelection.setAdapter(aa);
        tv =(TextView) view.findViewById(R.id.db_result);
        spn_tableSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String query = "SELECT * FROM " + table[i];
                Cursor c = dv.query(query);
                String result = "";
                while (c.moveToNext()) {
                    switch (i) {
                        case 0:
                            result += String.format(table_format[i], c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                            break;
                        case 1:
                            result += String.format(table_format[i], c.getString(0), c.getString(1), c.getDouble(2));
                    }
                }
                tv.setText(result);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }






}
