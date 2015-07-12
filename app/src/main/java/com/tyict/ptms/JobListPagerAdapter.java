package com.tyict.ptms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.tyict.ptms.JobService.JobList_Fragment;

/**
 * Created by RAYMOND on 7/12/2015.
 */
public class JobListPagerAdapter extends FragmentPagerAdapter
{

    FragmentManager fm;
    private String tabTitles[] = {
            "Pending", "Follow-up", "Completed", "Postponed", "Canceled"
    };
    private String statusArray[] = {
            "pending", "follow-up", "completed", "postponed", "cancelled"
    };
    private Fragment fragment[];
    public JobListPagerAdapter(FragmentManager fm)
    {
        super(fm);
        this.fm = fm;

        fragment = new Fragment[tabTitles.length];
        String vName  = "status";
        for(int i = 0; i < fragment.length; i++)
        {
            Bundle bundle = new Bundle();
            bundle.putString(vName, statusArray[i]);
            Fragment f = new JobList_Fragment();
            f.setArguments(bundle);
            fragment[i] =  f;
        }
    }


    @Override
    public android.support.v4.app.Fragment getItem(int position)
    {
        return fragment[position];
    }


   @Override
    public int getCount()
    {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }
}
