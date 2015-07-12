package com.tyict.ptms.Other;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.text.format.Time;
import android.os.Handler;

import com.tyict.ptms.R;

import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class Timer_Fragment extends Fragment implements View.OnClickListener {

    private View _this;
    private TextView tvShowStartTime;
    private TextView tvTimer;
    private TextView tvShowEndTime;
    private TextView tvTotal;
    private Button btnStart;
    private Button btnEnd;
    private Button btnReset;
    private Button btnPause;
    private int h, m,s;
    CounterClass timer;
    boolean isEnd = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.timer_layout, container, false);
        initVarible();

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        timer = new CounterClass(180000, 1000);

        return _this;
    }

    private void initVarible() {

        tvTimer = (TextView) _this.findViewById(R.id.tvTimer);
        tvShowStartTime = (TextView) _this.findViewById(R.id.tvShowStartTime);
        tvShowEndTime = (TextView) _this.findViewById(R.id.tvShowEndTime);
        tvTotal = (TextView) _this.findViewById(R.id.tvTotal);
        btnStart = (Button) _this.findViewById(R.id.btnStart);
        btnReset = (Button) _this.findViewById(R.id.btnReset);
        btnPause = (Button) _this.findViewById(R.id.btnPause);
        btnEnd = (Button) _this.findViewById(R.id.btnEnd);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
           /* String hsm = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            //tvTimer.setText(hsm);*/
            s += 1;
            if (s == 60) {
                m += 1;
                s = 0;
            }
            if (m == 60) {
                m = 0;
                s += 1;
            }
            tvTimer.setText(getTotalTimer());
        }

        @Override
        public void onFinish() {
            tvTimer.setText("00:00:00");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                timer.start();
                Time timeStart = new Time();
                timeStart.setToNow();
                if (isEnd) {
                    tvShowStartTime.setText("Start Time: " + timeStart.hour + ":" + timeStart.minute);
                    tvShowEndTime.setText("End Time");
                    isEnd = false;
                    tvTotal.setText("");
                }
                btnStart.setEnabled(false);
                btnPause.setEnabled(true);
                break;
            case R.id.btnPause:
                timer.cancel();
                btnPause.setEnabled(false);
                btnStart.setEnabled(true);
                break;
            case R.id.btnReset:
                isEnd = true;
                timer.cancel();
                s = 0;
                m = 0;
                h = 0;
                tvTotal.setText("");
                btnPause.setEnabled(false);
                btnStart.setEnabled(true);
                tvTimer.setText("00:00:00");
                tvShowEndTime.setText("End Time:");
                tvShowStartTime.setText("Start Time:");
                break;
            case R.id.btnEnd:
                timer.cancel();
                tvTotal.setText("Total: " + getTotalTimer());
                s = 0;
                m = 0;
                h = 0;
                Time timeEnd = new Time();
                btnPause.setEnabled(false);
                btnStart.setEnabled(true);
                timeEnd.setToNow();
                tvTimer.setText("00:00:00");
                if (!isEnd){
                    tvShowEndTime.setText("End Time: " + timeEnd.hour + ":" + timeEnd.minute);
                    isEnd = true;
                }
                break;
        }
    }

    protected String getTotalTimer() {
        String total = "";
        if (h < 10)
            total += "0" + h + ":";
        else
            total += h + ":";
        if (m < 10)
            total += "0" + m + ":";
        else
            total += m + ":";
        if (s < 10)
            total += "0" + s;
        else
            total += s;

        return total;
    }
}