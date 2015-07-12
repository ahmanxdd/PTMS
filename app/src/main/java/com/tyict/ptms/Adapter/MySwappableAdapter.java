package com.tyict.ptms.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.GitHub.swipe.SwipeLayout;
import com.tyict.ptms.GitHub.swipe.adapters.BaseSwipeAdapter;
import com.tyict.ptms.JobService.ListData;
import com.tyict.ptms.NonStoppable;
import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.ArrayList;

public class MySwappableAdapter extends BaseSwipeAdapter
{

    ArrayList<ListData> myList = new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    ArrayList dataSet;


    public MySwappableAdapter(Context context, ArrayList<ListData> myList)
    {
        this.myList = myList;
        this.context = context;
        dataSet = myList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout)
    {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public int getSwipeLayoutResourceId(int position)
    {
        return R.id.swipe;
    }

    @Override
    public int getCount()
    {
        return myList.size();
    }

    @Override
    public ListData getItem(int position)
    {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View generateView(final int position, ViewGroup parent)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        final Button deleteBtn = (Button) v.findViewById(R.id.delete);
        final TextView tv = (TextView) v.findViewById(R.id.tvJobNo);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));

            deleteBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (tv.getText().toString().equals(NonStoppable.startingJob))
                    {
                        Toast.makeText(context,"The job is started, please stop first",Toast.LENGTH_LONG).show();
                        return;
                    }

                    DatabaseView.exec("UPDATE ServiceJob SET jobStatus = 'cancelled' WHERE jobNo = '" + tv.getText().toString() + "'") ;
                    dataSet.remove(position);
                    notifyDataSetChanged();
                    mItemManger.closeAllItems();
                }
            });

        return v;

    }


    @Override
    public void fillValues(int position, View convertView)
    {
        final MyViewHolder mViewHolder;


            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


        ListData currentListData = getItem(position);
        mViewHolder.tvJobNo.setText(currentListData.getId());
        mViewHolder.tvJobProblem.setText(currentListData.getProblem());
        mViewHolder.tvJobStartDataTime.setText(currentListData.getDatatime());
        mViewHolder.tvJobStatus.setText(currentListData.getStatus());

        if (mViewHolder.tvJobStatus.getText().toString().equalsIgnoreCase("pending"))
        {
            convertView.setBackgroundColor(Color.rgb(0xA5, 0xD6, 0xA7));
            mViewHolder.imIcholder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flag_white_24dp));
        } else
        {
            mViewHolder.imIcholder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_white_24dp));
            convertView.setBackgroundColor(Color.rgb(0xBD, 0xBD, 0xBD));

        }

        if (mViewHolder.tvJobNo.getText().toString().equals(NonStoppable.startingJob))
        {
            mViewHolder.imIcholder.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_data_usage_white_24dp));
            mViewHolder.tvJobStatus.setText("On going");
            convertView.setBackgroundColor(Color.rgb(0xFF, 0xAB, 0x91));
        }
    }

    public class MyViewHolder
    {
        TextView tvJobNo, tvJobProblem, tvJobStartDataTime, tvJobStatus;
        ImageView imIcholder;

        public MyViewHolder(View item)
        {
            tvJobNo = (TextView) item.findViewById(R.id.tvJobNo);
            tvJobProblem = (TextView) item.findViewById(R.id.tvJobProblem);
            tvJobStatus = (TextView) item.findViewById(R.id.tvJobStatus);
            tvJobStartDataTime = (TextView) item.findViewById(R.id.tvJobStartDataTime);
            imIcholder = (ImageView) item.findViewById(R.id.iv_ic_holder);
        }
    }
}