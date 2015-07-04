package com.tyict.ptms;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

/**
 * Created by Lee on 4/7/15.
 */


public class Test extends Activity {
    String staffID = "1001";
    String[] jobIDs;
    String[] jobProblems;
    String[] jobStatus;
    String[] jobStartDates;
    String[] jobTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joblist_layout);
        /*ListView lvJobList = (ListView) findViewById(R.id.lvJobList);*/
        DatabaseView dbv = new DatabaseView();
        Cursor cursor = dbv.refreshJobList("1002");

        String tt = "";
        while (cursor.moveToNext())
            tt += cursor.getString(cursor.getColumnIndex("requestDate")) + "\t";
        ((TextView)findViewById(R.id.tvShow)).setText(tt + "sdfsdf");

        /*for(int i=0; i<cursor.getCount(); i++)
        {
            Log.d("", String.valueOf(cursor));
            if(cursor.moveToNext())
            {
                System.out.println(cursor);
                jobIDs[i] = cursor.getString(cursor.getColumnIndex("jobNo"));
                jobProblems[i] = cursor.getString(cursor.getColumnIndex("jobProblem"));
                jobStatus[i] = cursor.getString(cursor.getColumnIndex("jobStatus"));
                jobStartDates[i] = cursor.getString(cursor.getColumnIndex("visitDate"));
                jobTimes[i] = cursor.getString(cursor.getColumnIndex("jobStartTime"));
                tt += jobIDs.toString() + "\t" + jobProblems.toString() + "\t" + jobStatus.toString() +
                        "\t" + jobStartDates.toString() +"\t" + jobTimes.toString() + "\n";
            }
        }
        Toast.makeText(this, tt, Toast.LENGTH_SHORT).show();*/
    }
}
