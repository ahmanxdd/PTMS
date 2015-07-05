package com.tyict.ptms;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.dreams.DreamService;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.tyict.ptms.dataInfo.DatabaseView;

import java.io.InputStreamReader;

public class Main_activity extends ActionBarActivity{

    SearchView searchView;
    PagerAdapter adapter;
    ViewPager mViewPager;
    String searchKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        adapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int arg0) {
            switch (arg0) {
                case 0:
                    return new Joblist_activity();
                case 1:
                    return new Test2_activity();
                case 2:
                    return new Test3_activity();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            String titulo = null;
            switch (position) {
                case 0:
                    titulo = "Job List";
                    break;
                case 1:
                    titulo = "Job Record";
                    break;
                case 2:
                    titulo = "Test3";
                    break;
            }
            return titulo;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.joblist_actionbar, menu);
       // return true;
        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.menu_main, menu);
        MenuItem search_action_item = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(search_action_item);
        MenuItemCompat.collapseActionView(search_action_item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.equals("")) {
                    show();
                    searchView.onActionViewCollapsed();
                    return false;
                } else
                    return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    searchView.onActionViewCollapsed();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_reply:
                action_reply_click();
                break;
            case R.id.action_cached:
                action_cached_click();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void action_cached_click() {
        refreshListView();
        Toast.makeText(this, "Cached finish!", Toast.LENGTH_SHORT).show();
    }

    private void action_reply_click() {
        Joblist_activity.listViewSQL = "SELECT * FROM ServiceJob" ;
        refreshListView();
    }

    public void show(){         ///do something when press submit on searchview
        searchKey = searchView.getQuery().toString();
        Toast.makeText(getApplication(), searchView.getQuery(), Toast.LENGTH_SHORT).show();
        String searchSQL= "SELECT * FROM ServiceJob WHERE " + " jobNo LIKE '%" + searchKey + "%' OR " +
                " jobProblem LIKE '%" + searchKey + "%' OR " +
                " jobStatus LIKE '%" + searchKey + "%'";

        Joblist_activity.listViewSQL = searchSQL;
        refreshListView();
    }

    public void refreshListView(){
        Joblist_activity.fillDetail();
        Joblist_activity.getDataInList();
        Joblist_activity.lvJobList.setAdapter(new MyBaseAdapter(Joblist_activity.context, Joblist_activity.myList));
    }
}