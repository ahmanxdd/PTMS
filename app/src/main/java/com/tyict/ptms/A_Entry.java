package com.tyict.ptms;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.widget.DrawerLayout;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.tyict.ptms.DataInDatabase.ViewAll;
import com.tyict.ptms.JobService.JobList_Fragment;
import com.tyict.ptms.Other.F_productForGraph;
import com.tyict.ptms.Other.F_productIssues;
import com.tyict.ptms.Other.f_companyDetails;
import com.tyict.ptms.JobService.F_Joblist;

/**
 * Created by RAYMOND on 7/5/2015.
 */

public class A_Entry extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ListView listView;

    private FrameLayout frameLayout;
    private Fragment companyDetails, productIssues, servicePage, jobServilePage; //reuseable
    FragmentManager f_manager;
    FragmentTransaction ft;
    private static final String[] menuItems =
            {
                   "Job List", "Product Issues", "Company Details", "ServiceTime Graph", "Lee Testing", "View All Data"
            };

    enum Menu {
        Job_List, Product_Issues, Comapny_Details, Avg_Time_Graph, Lee_Testing, View_All_Data
    }

    public void goToCamera() {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent cameraIntente = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    private ActionBarDrawerToggle drawerListener;


    public void goToFragment(Menu m) {
        goToFragment(m.ordinal());
    }

    private void goToFragment(int index) {
        listView.setItemChecked(index, true);
        setTitle(listView.getItemAtPosition(index).toString());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new F_productForGraph();


        switch (index) {
            case 0:
                f = jobServilePage;
                break;
            case 1:
                f = productIssues;
                break;
            case 2:
                f = companyDetails;
                break;
            case 3:
                f = servicePage;
                break;
            case 4:
                f = new Layout_testing();
                break;
            case 5:
                f = new ViewAll();

        }

        ft = f_manager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(frameLayout.getId(), f);
        ft.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        companyDetails = new f_companyDetails();
        productIssues = new F_productIssues();
        servicePage = new F_productForGraph();
        jobServilePage = new JobList_Fragment();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.menuList);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.a_main_menu_list_items_style, menuItems));
        listView.setOnItemClickListener(this);
        frameLayout = (FrameLayout) findViewById(R.id.mainContent);
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                goToFragment(selectedIndex);
            }
        };
        drawerLayout.setDrawerListener(drawerListener);
        //   getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        f_manager = getSupportFragmentManager();
        drawerListener.syncState();
        ft = f_manager.beginTransaction();
        ft.replace(frameLayout.getId(), jobServilePage);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    public void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedIndex = i;
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private int selectedIndex = 0;


}
