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
import com.tyict.ptms.JobService.Add_ServiceJob_Fragment;
import com.tyict.ptms.JobService.JobList_Fragment;
import com.tyict.ptms.Other.F_productForGraph;
import com.tyict.ptms.Other.F_productIssues;
import com.tyict.ptms.Other.f_companyDetails;


/**
 * Created by RAYMOND on 7/5/2015.
 */

public class A_Entry extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ListView listView;

    private FrameLayout frameLayout;
    public Fragment productIssues, graphicForProducts, jobServilePage; //reuseable
    public Fragment companyDetails;
    FragmentManager f_manager;
    FragmentTransaction ft;
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
    private static final String[] menuItems =
            {
                    "Job List", "Add new Service","Product Issues", "Company Details", "ServiceTime Graph", "View All Data", "Logout"
            };

    private void goToFragment(int index) {
        listView.setItemChecked(index, true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new F_productForGraph();
        setTitle(menuItems[index]);

        switch (index) {
            case 0:
                f = jobServilePage;
                break;
            case 1:
                f = new Add_ServiceJob_Fragment();
                break;
            case 2:
                f = productIssues;
                break;
            case 3:
                f =companyDetails;
                break;
            case 4:
                f = graphicForProducts;
                break;
            case 5:
                f = new ViewAll();
                break;
            case 6:
                NoStopable.lc.logout();
                this.finish();
                break;

        }

        ft = f_manager.beginTransaction();
        f_manager.popBackStack();
        ft.replace(frameLayout.getId(), f);
        ft.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        companyDetails = new f_companyDetails();
        productIssues = new F_productIssues();
        graphicForProducts = new F_productForGraph();
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
        ft = f_manager.beginTransaction();;
        ft.replace(frameLayout.getId(), jobServilePage);
        ft.commit();
        setTitle(menuItems[0]);
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
        this.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedIndex = i;
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private int selectedIndex = 0;


}
