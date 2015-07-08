package com.tyict.ptms.JobService;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tyict.ptms.SignView;

/**
 * Created by RAYMOND on 7/9/2015.
 */
public class Sign extends Fragment {
    @Nullable
    View _this;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        View  v = new SignView(getActivity());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200, 1f));

        ll.addView(new SignView(getActivity()));
        Button btn  =new Button(getActivity());
        btn.setText("Submit");
        ll.addView(btn);
        return ll;

    }
}
