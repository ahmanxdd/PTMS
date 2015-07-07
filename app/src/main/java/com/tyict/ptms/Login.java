package com.tyict.ptms;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tyict.ptms.dataInfo.LoginControl;


public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btn_login_click(View v)
    {
        startActivity(new Intent().setClass(Login.this, A_Entry.class));
        finish();
        String userID = ((EditText)findViewById(R.id.et_loginID)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_password)).getText().toString();
        if(LoginControl.login(userID,password))
        {
            Intent i = new Intent(this, A_Entry.class);
            startActivity(i);
        }
        else
        {
            ((TextView)findViewById(R.id.message)).setText("Login Failure, check your password and loginID");
        }
    }

}
