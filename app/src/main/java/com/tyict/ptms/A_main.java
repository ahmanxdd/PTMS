package com.tyict.ptms;



import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by RAYMOND on 7/5/2015.
 */

public class A_main extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ListView listView;

    private FrameLayout frameLayout;
    private Fragment companyDetails, productIssues, servicePage; //reuseable
    private static final String[] menuItems =
            {
                   "Job List", "Product Issues", "Company Details", "ServiceTime Graph", "Lee Testing"
            };

    private ActionBarDrawerToggle drawerListener;

    public void goToFragment(int index) {
        listView.setItemChecked(index, true);
        setTitle(listView.getItemAtPosition(index).toString());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new F_productForGraph();

        switch (index) {
            case 0:
                f = new JobList_Fragment();
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
                f = new JobDetail_Fragment();

        }

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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.menuList);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.a_main_menu_list_items_style, menuItems));
        listView.setOnItemClickListener(this);
        frameLayout = (FrameLayout)findViewById(R.id.mainContent);
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerListener.syncState();
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
        goToFragment(i);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }
}
