package com.tyict.ptms;


import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.DataInDatabase.ViewAll;
import com.tyict.ptms.JobService.Add_ServiceJob_Fragment;
import com.tyict.ptms.JobService.JobList_Fragment;
import com.tyict.ptms.Other.F_productForGraph;
import com.tyict.ptms.Other.F_productIssues;
import com.tyict.ptms.Other.f_companyDetails;

import java.util.HashMap;


/**
 * Created by RAYMOND on 7/5/2015.
 */

public class A_Entry extends ActionBarActivity implements AdapterView.OnItemClickListener
{
    private DrawerLayout drawerLayout;
    private ListView lv_Menu;
    private CustomerAdapter adapter;
    private FrameLayout fmlo_Mother_Of_Fragment;
    private Fragment f_Product_Issue, f_Graphics, f_Job_List, f_Company_Details, f_Add_Service, f_DataBase; //reuseable
    private FragmentManager fmmg_Father_Of_Fragment;
    private FragmentTransaction fmtst_Bridge_Of_Fragment;
    private static final String[] menuItems =
            {
                    "Jobs", "Add Job", "Pre.Issues", "Company", "Graph", "View All", "Timer", "Logout"
            };


    private void initFragmentComponet()
    {

        fmlo_Mother_Of_Fragment = (FrameLayout) findViewById(R.id.mainContent);
        fmmg_Father_Of_Fragment = getSupportFragmentManager();
        f_Product_Issue = new F_productIssues();
        f_Add_Service = new Add_ServiceJob_Fragment();
        f_Graphics = new F_productForGraph();
        f_Job_List = new JobList_Fragment();
        f_Company_Details = new f_companyDetails();

    }

    @Override
    public void onBackPressed()
    {
        fmmg_Father_Of_Fragment = getSupportFragmentManager();
        if (fmmg_Father_Of_Fragment.getBackStackEntryCount() > 0)
            fmmg_Father_Of_Fragment.popBackStack();
        else
            super.onBackPressed();
        return;
    }


    private ActionBarDrawerToggle drawerListener;

    private void setUpDrawer()
    {
        lv_Menu = (ListView) findViewById(R.id.menuList);
        adapter = new CustomerAdapter(this, menuItems);
        //  lv_Menu.setAdapter(new ArrayAdapter<>(this, R.layout.a_main_menu_list_items_style, menuItems));
        lv_Menu.setAdapter(adapter);
        lv_Menu.setOnItemClickListener(this);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                goToFragment(selectedIndex);
            }

        };


        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerListener.syncState();


    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        initFragmentComponet();
        setUpDrawer();
    }


    public void goToCamera()
    {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false)
        {
            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent cameraIntente = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }


    private void goToFragment(int index)
    {
        lv_Menu.setItemChecked(index, true);
        fmtst_Bridge_Of_Fragment = getSupportFragmentManager().beginTransaction();
        Fragment f_tmp = null;

        switch (index)
        {
            case 0:
                f_tmp = f_Job_List;
                break;
            case 1:
                f_tmp = new Add_ServiceJob_Fragment();
                break;
            case 2:
                f_tmp = f_Product_Issue;
                break;
            case 3:
                f_tmp = f_Company_Details;
                break;
            case 4:
                f_tmp = f_Graphics;
                break;
            case 5:
                f_tmp = new ViewAll();
                break;
            case 6:
                f_tmp = new Timer_Fragment();
                break;
            case 7:
                NoStopable.lc.logout();
                this.finish();
                break;
            default:
                break;

        }
        setTitle(menuItems[index]);

        for (int i = 0; i < fmmg_Father_Of_Fragment.getBackStackEntryCount(); ++i)
        {
            fmmg_Father_Of_Fragment.popBackStack();
        }
        if (f_tmp != null)
        {
            fmtst_Bridge_Of_Fragment
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.slide_out_right)
                    .replace(fmlo_Mother_Of_Fragment.getId(), f_tmp)
                    .commit();
        }
    }


    public void transferTo(Fragment f_tmp, boolean backStack)
    {
        fmtst_Bridge_Of_Fragment = fmmg_Father_Of_Fragment.beginTransaction();
        if (backStack)
            fmtst_Bridge_Of_Fragment
                    .addToBackStack(null);

        fmtst_Bridge_Of_Fragment
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.slide_out_right)
                .replace(fmlo_Mother_Of_Fragment.getId(), f_tmp)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerListener.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        selectedIndex = i;
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private int selectedIndex = 0;


}

class CustomerAdapter extends BaseAdapter
{
    String menuItems[];
    private Context _context;
    int[] ic_list = {R.drawable.ic_view_array_white_24dp, R.drawable.ic_note_add_white_24dp, R.drawable.ic_info_outline_white_24dp, R.drawable.ic_description_white_24dp, R.drawable.ic_trending_up_white_24dp,
            R.drawable.ic_view_array_white_24dp, R.drawable.ic_schedule_white_24dp, R.drawable.ic_change_history_white_24dp};

    public CustomerAdapter(Context context, String[] items)
    {
        _context = context;
        menuItems = items;
    }


    @Override
    public int getCount()
    {
        return menuItems.length;
    }

    @Override
    public Object getItem(int i)
    {
        return menuItems[i];
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View row = null;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, viewGroup, false);
        } else
        {
            row = view;
        }

        TextView tv_title = (TextView) row.findViewById(R.id.textView);
        ImageView iv_title = (ImageView) row.findViewById(R.id.imageView);
        tv_title.setText(menuItems[i]);
        iv_title.setImageResource(ic_list[i]);
        return row;
    }
}
