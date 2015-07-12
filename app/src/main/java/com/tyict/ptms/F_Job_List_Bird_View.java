package com.tyict.ptms;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by RAYMOND on 7/12/2015.
 */
public class F_Job_List_Bird_View extends Fragment
{
    private ViewPager pager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.f_job_list_page_view, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new JobListPagerAdapter(getChildFragmentManager()));
        return view;
    }
}
