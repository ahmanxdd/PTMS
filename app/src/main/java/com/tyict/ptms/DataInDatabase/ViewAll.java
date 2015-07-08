package com.tyict.ptms.DataInDatabase;


import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by RAYMOND on 7/4/2015.
 */
public class ViewAll extends Fragment {

    Spinner spn_tableSelection;
    String[] table;
    LinearLayout cusorView ;
    String[] table_format;
    DatabaseView dv;
    TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dv = new DatabaseView();
        table =  dv.getAllTable();
        table_format = dv.getTableFormat();
        View view = inflater.inflate(R.layout.fragment_view_all, container, false);
        cusorView = (LinearLayout)view.findViewById(R.id.viewAllData_layout);
        spn_tableSelection = (Spinner)view.findViewById(R.id.spn_tableSelection);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, table);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_tableSelection.setAdapter(aa);
        tv =(TextView) view.findViewById(R.id.rowofData);
        spn_tableSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String query = "SELECT * FROM " + table[i];
                Cursor c = dv.query(query);
                RowColumnManager rcm = new RowColumnManager();
                while (c.moveToNext()) {
                    String[] columnsName = c.getColumnNames();
                    rcm.addTitle(c.getString(0));
                    for(int j = 1; j < columnsName.length;j++)
                    {
                        int type = c.getType(j);
                        rcm.addTVColumn(c.getColumnName(j) + ": ");
                        if(type == 3) //String
                            rcm.addTVColumn(c.getString(j),1);
                        else if(type == 2) //Float
                            rcm.addTVColumn(Float.toString(c.getFloat(j)),1);
                        else if(type == 1) //int
                            rcm.addTVColumn(Integer.toString(c.getInt(j)),1);
                        rcm.addRow();
                    }
                }
                cusorView.removeViewAt(cusorView.getChildCount() - 1);
                cusorView.addView(rcm.getTableLayout());
                tv.setText("\tNo. of Result: " + c.getCount());


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private class RowColumnManager
    {
        private TableLayout layout = new TableLayout(getActivity());
        private List<TableRow> tr = new ArrayList<>();
        private TableRow tmp_row;
        private int rowCount,colCount = 0;
        public RowColumnManager addTitle(String title)
        {
            tmp_row = new TableRow(getActivity());
            TextView _title = new TextView(getActivity());
            _title.setText(title);rowCount++;
            _title.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            _title.setGravity(Gravity.CENTER);
            _title.setTypeface(null, Typeface.BOLD);
            _title.setBackgroundColor(Color.RED);
            tmp_row.addView(_title);
            addRow();
            return this;
        }

        public RowColumnManager addRow()
        {
            layout.addView(tmp_row);
            colCount=0;
            rowCount++;
            tmp_row = new TableRow(getActivity());
            return this;
        }

        public RowColumnManager addTVColumn(String s)
        {
            return addTVColumn(s,0);
        }

        public RowColumnManager addTVColumn(String s,int weight)
        {
            TextView tv =new TextView(getActivity());
            tv.setText(s);
            tv.setEllipsize(null);
            tv.setSingleLine(false);
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.column = colCount;
            params.weight = weight;
            params.setMargins(5,2,2,2);
            colCount++;
            tmp_row.addView(tv,params);
            return this;
        }
        public TableLayout getTableLayout()
        {
            return layout;
        }

    }

}


