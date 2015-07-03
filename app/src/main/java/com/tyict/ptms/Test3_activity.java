package com.tyict.ptms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;

public class Test3_activity extends Fragment {

    Button btnLogout;
    View rootView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.test3_layout, container, false);

        return rootView;
    }
}