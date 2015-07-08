package com.tyict.ptms;

import android.app.Application;
import android.content.Intent;

import com.tyict.ptms.dataInfo.LoginControl;

/**
 * Created by RAYMOND on 7/8/2015.
 */
public class NoStopable extends Application {
    public static boolean opened = false;
    public static LoginControl lc;
    public static Intent i;
    public static String selectedCompanyDetails = "x";
    public static String startingJob = null;
}

