package com.tyict.ptms.dataInfo;

import android.database.Cursor;

/**
 * Created by RAYMOND on 7/2/2015.
 */
public class LoginControl {

    private class Technician {
        public String staffNo, stafLogin, staffName;
    }

    private enum LoginState {
        UNLOGIN, ISLOGIN;
    }

    private LoginState state;
    private Technician _loggedInStaff;

    public boolean isLogin()
    {
        return state == LoginState.ISLOGIN;
    }

    public String getStaffName()
    {
        if(state == LoginState.ISLOGIN)
            return _loggedInStaff.staffName;
        else
            return null;
    }

    public String getStaffID()
    {
        if(state == LoginState.ISLOGIN) return _loggedInStaff.staffNo;
        else return null;
    }

    public boolean login(String username, String password) {
        DatabaseView dbv = new DatabaseView();
        if(state == LoginState.ISLOGIN) return  false; // Logined user;
        Cursor c = dbv.query("SELECT * FROM Technician WHERE staffLogin ='" + username + "' AND staffPswd = '" + password + "'");
        if (c.getCount() <= 0) {
            state = LoginState.UNLOGIN;
            _loggedInStaff = null;
            return false;
        } else {
            c.moveToNext();
            _loggedInStaff = new Technician();
            _loggedInStaff.staffNo = c.getString(c.getColumnIndex("staffNo"));
            _loggedInStaff.stafLogin = c.getString(c.getColumnIndex("staffLogin"));
            _loggedInStaff.staffName = c.getString(c.getColumnIndex("staffName"));
            state = LoginState.ISLOGIN;
            return true;
        }
    }

    public boolean logout() {
        if (state == LoginState.ISLOGIN) {
            _loggedInStaff = null;
            return true;
        } else
            return false;
    }
}