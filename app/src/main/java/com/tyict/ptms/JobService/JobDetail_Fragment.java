package com.tyict.ptms.JobService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.A_Entry;
import com.tyict.ptms.NoStopable;
import com.tyict.ptms.Other.f_companyDetails;
import com.tyict.ptms.R;
import com.tyict.ptms.NoStopable;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.TreeMap;


public class JobDetail_Fragment extends Fragment {
    View _this;
    private Bundle bundle;
    private String selectedjobNo;
    private TextView jobNo;
    private TextView jobProblem;
    private Spinner jobStatus;
    private String[] jobStatusItem = {"completed", "follow-up", "pending", "cancelled", "postponed"};
    private TextView jobSerialNo;
    private TextView jobRequestDate;
    private TextView jobVisitDate;
    private TextView jobStartTime;
    private Uri fileURI;
    private Button btn_photo;
    private Button btn_cancelJob;
    private Button btn_postpone;
    private Button btn_photo, btn_startTimer;
    private TextView jobEndTime;
    private TextView jobRemark;
    private AlertDialog.Builder editDialog;

    private EditText.OnLongClickListener goToEdit = new EditText.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            final EditText et = new EditText(getActivity());
            et.setText(((TextView) view).getText().toString());
            final View tmp = view;
            editDialog.setView(et);
            int id = view.getId();
            final String column;
            if (id == jobProblem.getId())
                column = "jobProblem";
            else
                column = "remark";

            final String jobNoText = jobNo.getText().toString();
            editDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((TextView) tmp).setText(et.getText().toString());
                    DatabaseView.exec("UPDATE ServiceJob SET " + column + " = '" + et.getText().toString() + "' WHERE jobNo = '" + jobNoText + "'");
                }
            });
            editDialog.show();

            return true;
        }
    };


    @Nullable
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.jobdetail_layout, container, false);
        initVariable();
        setStatusItems();
        setjobDetail();
        jobStatus.setEnabled(false);
        //Raymond Go!
        if(!jobStatus.getSelectedItem().toString().equals("pending")) {
            btn_startTimer.setText("This job is ended");
            btn_startTimer.setTextColor(Color.GRAY);
            jobStatus.setEnabled(true);
            btn_startTimer.setEnabled(false);
            setOnClickListenerForTimer();
        }
        else if (NoStopable.startingJob != null) {
            if (NoStopable.startingJob.equals(jobNo.getText().toString())) {
                btn_startTimer.setText("FINSH");
                setOnClickListenerForTimer();
            } else {
                    btn_startTimer.setText("You started " + NoStopable.startingJob);
                btn_startTimer.setTextColor(Color.GRAY);
            }
            btn_startTimer.setTextColor(Color.RED);
        } else {
            setOnClickListenerForTimer();
        }


        btn_photo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //fileURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        fileURI = getImageUri();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI); // set the image file name
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
        );

        btn_cancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<jobStatusItem.length; i++)
                    if(jobStatusItem[i].equals("cancelled"))
                    {
                        jobStatus.setSelection(i);
                        DatabaseView.exec("UPDATE ServiceJob SET jobStatus = 'cancelled' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                        Toast.makeText(getActivity(), "Successful change the status to cancel!", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        btn_postpone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<jobStatusItem.length; i++)
                    if(jobStatusItem[i].equals("postponed"))
                    {
                        jobStatus.setSelection(i);
                        DatabaseView.exec("UPDATE ServiceJob SET jobStatus = 'postponed' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                        Toast.makeText(getActivity(), "Successful change the status to postpone!", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        editDialog = new AlertDialog.Builder(getActivity());
        editDialog.setTitle("Edit");
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        jobProblem.setOnLongClickListener(goToEdit);
        jobRemark.setOnLongClickListener(goToEdit);
        //Raymond End!
        return _this;
    }

    private Uri getImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM", "jobNo_" + jobNo.getText().toString() + "_" + timeStamp + ".jpg");
        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(), "Successful take the photo!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            } else {
                Toast.makeText(getActivity(), "Unsuccessful take the photo!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setOnClickListenerForTimer() {
        btn_startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar date = Calendar.getInstance();
                formatter = new SimpleDateFormat("HH:mm");
                String time = formatter.format(date.getTime()).toString();
                formatter = new SimpleDateFormat("dd/MM/yyyy");
                String vdate = formatter.format(date.getTime()).toString();

                if (NoStopable.startingJob != null) {
                    bgTimer.cancel(true);
                    btn_startTimer.setText("START");
                    jobEndTime.setText(time);
                    DatabaseView.exec("UPDATE ServiceJob SET jobEndTime = '" + time + "' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                    btn_startTimer.setEnabled(false);
                    btn_startTimer.setText("This job is ended");
                    btn_startTimer.setTextColor(Color.GRAY);
                    NoStopable.startingJob = null;
                } else {
                    bgTimer = new BackGroundTimer();
                    bgTimer.execute();
                    jobStartTime.setText(time);
                    btn_startTimer.setTextColor(Color.RED);
                    btn_startTimer.setText("FINSH");
                    jobVisitDate.setText(vdate);
                    NoStopable.startingJob = jobNo.getText().toString();
                    DatabaseView.exec("UPDATE ServiceJob SET jobStartTime = '" + time + "' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                    DatabaseView.exec("UPDATE ServiceJob SET visitDate = '" + vdate + "' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                }
                formatter = new SimpleDateFormat("HH-mm-ss");
            }
        });
    }

    private static BackGroundTimer bgTimer;

    private void setStatusItems() {
        jobStatus.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, jobStatusItem));
        jobStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DatabaseView.exec("UPDATE ServiceJob SET jobStatus " + " = '" + jobStatus.getSelectedItem().toString() + "' WHERE jobNo = '" + jobNo.getText().toString() + "'");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initVariable() {
        bundle = getArguments();
        selectedjobNo = bundle.getString("selectedJobNo");
        jobCompany = (TextView) _this.findViewById(R.id.jobDetail_companyName);
        jobNo = (TextView) _this.findViewById(R.id.jobDetail_jobNo);
        jobProblem = (TextView) _this.findViewById(R.id.jobDetail_problem);
        jobStatus = (Spinner) _this.findViewById(R.id.jobDetail_status);
        jobSerialNo = (TextView) _this.findViewById(R.id.jobDetail_serialNo);
        jobRequestDate = (TextView) _this.findViewById(R.id.jobDetail_requestDate);
        jobVisitDate = (TextView) _this.findViewById(R.id.jobDetail_visitDate);
        jobStartTime = (TextView) _this.findViewById(R.id.jobDetail_startTime);
        productName = (TextView) _this.findViewById(R.id.jobDetail_product);
        jobEndTime = (TextView) _this.findViewById(R.id.jobDetail_endTime);
        jobRemark = (TextView) _this.findViewById(R.id.jobDetail_remark);
        btn_photo = (Button) _this.findViewById(R.id.btn_photo);
        btn_cancelJob = (Button) _this.findViewById(R.id.jobDetail_btnCancelJob);
        btn_postpone = (Button) _this.findViewById(R.id.jobDetail_btnPostpone);
        btn_startTimer = (Button) _this.findViewById(R.id.btn_startJob);
    }

    private String companyID, prodctID;

    public void setjobDetail() {
        Cursor cursor = DatabaseView.query("SELECT * FROM ServiceJob, Purchase, Product, Company WHERE jobNo ='" + selectedjobNo + "'" + " AND Purchase.prodNo = Product.prodNo AND Purchase.comNo = Company.comNo AND Purchase.serialNo = ServiceJob.serialNo");
        cursor.moveToFirst();
        String _jobProblem = cursor.getString(cursor.getColumnIndex("jobProblem"));
        String _jobSerialNo = cursor.getString(cursor.getColumnIndex("serialNo"));
        String _jobRequestDate = cursor.getString(cursor.getColumnIndex("requestDate"));
        String _jobVisitDate = cursor.getString(cursor.getColumnIndex("visitDate"));
        String _jobStartTime = cursor.getString(cursor.getColumnIndex("jobStartTime"));
        String _jobEndTime = cursor.getString(cursor.getColumnIndex("jobEndTime"));
        String _jobRemark = cursor.getString(cursor.getColumnIndex("remark"));
        companyID = cursor.getString(cursor.getColumnIndex("comNo"));
        prodctID = cursor.getString(cursor.getColumnIndex("prodNo"));

        jobNo.setText(selectedjobNo);
        jobProblem.setText(_jobProblem);
        jobSerialNo.setText(_jobSerialNo);
        jobRequestDate.setText(_jobRequestDate);
        jobVisitDate.setText(_jobVisitDate);
        jobStartTime.setText(_jobStartTime);
        jobEndTime.setText(_jobEndTime);
        jobRemark.setText(_jobRemark);
        jobCompany.setText(cursor.getString(cursor.getColumnIndex("comName")));
        jobCompany.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager f_manager;
                        FragmentTransaction ft;
                        FrameLayout frameLayout = (FrameLayout) _this.getParent();
                        f_manager = getActivity().getSupportFragmentManager();
                        ft = f_manager.beginTransaction();
                        f_manager.popBackStack();
                        Fragment companyDetails = A_Entry.companyDetails;
                        ((f_companyDetails) companyDetails).getThisCompanyDetails(jobCompany.getText().toString());
                        ft.addToBackStack(null);

                        ft.replace(frameLayout.getId(), companyDetails);
                        ft.commit();
                    }
                }
        );
        productName.setText(cursor.getString(cursor.getColumnIndex("prodName")));


        for (int i = 0; i < jobStatusItem.length; i++)
            if (jobStatusItem[i].equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("jobStatus"))))
                jobStatus.setSelection(i);

    }

    SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
    private class BackGroundTimer extends AsyncTask<String, String, String> {

        private int notiID = 1;
        private Calendar time;
        private boolean stop = false;
        private NotificationCompat.Builder noti;
        NotificationManager nm;

        @Override
        protected void onPreExecute() {
            Date date = new Date();
            date.setTime(0);
            time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 0);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            noti = new NotificationCompat.Builder(getActivity());
            noti.setContentTitle("Starting Job").setContentText("HI").setSmallIcon(R.drawable.cast_ic_notification_0);
            nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        }

        protected String doInBackground(String... strings) {

            while (!isCancelled()) {
                try {
                    Thread.sleep(1000);
                    time.add(Calendar.SECOND, 1);
                    noti.setContentText("Starting Job " + jobNo.getText().toString() + " " + formatter.format(time.getTime()));
                    nm.notify(notiID, noti.build());
                } catch (Exception e) {

                }
            }
            nm.cancel(notiID);
            return null;
        }

    }

}


class Notif0action extends Activity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);
    }

    public void setText(String s) {
        tv.setText(s);
    }

}