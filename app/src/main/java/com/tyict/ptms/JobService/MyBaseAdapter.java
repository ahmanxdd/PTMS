package com.tyict.ptms.JobService;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tyict.ptms.R;

import java.util.ArrayList;

public class MyBaseAdapter extends BaseAdapter {

    ArrayList<ListData> myList = new ArrayList<>();
    LayoutInflater inflater;
    Context context;


    public MyBaseAdapter(Context context, ArrayList<ListData> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public ListData getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        ListData currentListData = getItem(position);
        mViewHolder.tvJobNo.setText(currentListData.getId());
        mViewHolder.tvJobProblem.setText(currentListData.getProblem());
        if(currentListData.getDatatime() == "")
            mViewHolder.tvJobStartDataTime.setText("Not start yet");
        else
            mViewHolder.tvJobStartDataTime.setText(currentListData.getDatatime());
        mViewHolder.tvJobStatus.setText(currentListData.getStatus());
        if (mViewHolder.tvJobStatus.getText().toString().equalsIgnoreCase("completed"))
            convertView.setBackgroundColor(Color.rgb(0xBD, 0xBD, 0xBD));
        else if (mViewHolder.tvJobStatus.getText().toString().equalsIgnoreCase("follow-up"))
            convertView.setBackgroundColor(Color.rgb(0xFF, 0xAB, 0x91));
        else if (mViewHolder.tvJobStatus.getText().toString().equalsIgnoreCase("pending"))
            convertView.setBackgroundColor(Color.rgb(0xA5, 0xD6, 0xA7));

        return convertView;
    }

    private class MyViewHolder {
        TextView tvJobNo, tvJobProblem, tvJobStartDataTime, tvJobStatus;

        public MyViewHolder(View item) {
            tvJobNo = (TextView) item.findViewById(R.id.tvJobNo);
            tvJobProblem = (TextView) item.findViewById(R.id.tvJobProblem);
            tvJobStatus = (TextView) item.findViewById(R.id.tvJobStatus);
            tvJobStartDataTime = (TextView) item.findViewById(R.id.tvJobStartDataTime);
        }
    }
}