package com.tyict.ptms.JobService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JobDetail_Fragment extends Fragment {
    View _this;
    private Bundle bundle;
    private String selectedjobNo;
    private TextView jobNo;
    private TextView jobProblem;
    private Spinner jobStatus;
    private String[] jobStatusItem = {"completed", "follow-up", "pending"};
    private TextView jobSerialNo;
    private TextView jobRequestDate;
    private TextView jobVisitDate;
    private TextView jobStartTime;
    private Uri fileURI;
    private Button btn_photo;
    private TextView jobEndTime;
    private TextView jobRemark;
    private AlertDialog.Builder editDialog;
    private EditText.OnLongClickListener goToEdit = new EditText.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view) {
            final EditText et = new EditText(getActivity());
            et.setText(((TextView) view).getText().toString());
            final View tmp = view;
            editDialog.setView(et);
            int id = view.getId();
            final String column;
            if(id == jobProblem.getId())
                column = "jobProblem";
            else
                column = "remark";

            final String jobNoText = jobNo.getText().toString();
            editDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((TextView) tmp).setText(et.getText().toString());
                    DatabaseView.exec("UPDATE ServiceJob SET " + column + " = '" + et.getText().toString() + "' WHERE jobNo = '" + jobNoText + "'" );
                }
            });
            editDialog.show();

            return true;
        }
    };


    @Nullable
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
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

        //Raymond Go!
        editDialog = new AlertDialog.Builder(getActivity());
        editDialog.setTitle("Edit");
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        btn_photo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI); // set the image file name
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
        );
        jobProblem.setOnLongClickListener(goToEdit);
        jobRemark.setOnLongClickListener(goToEdit);
        //Raymond End!
        return _this;
    }

    private void setStatusItems() {
        jobStatus.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, jobStatusItem));
        jobStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DatabaseView.exec("UPDATE ServiceJob SET jobStatus "  + " = '"  + jobStatus.getSelectedItem().toString() + "' WHERE jobNo = '" + jobNo.getText().toString() + "'");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initVariable() {
        bundle = getArguments();
        selectedjobNo = bundle.getString("selectedJobNo");
        jobNo = (TextView) _this.findViewById(R.id.jobDetail_jobNo);
        jobProblem = (TextView) _this.findViewById(R.id.jobDetail_problem);
        jobStatus = (Spinner) _this.findViewById(R.id.jobDetail_status);
        jobSerialNo = (TextView) _this.findViewById(R.id.jobDetail_serialNo);
        jobRequestDate = (TextView) _this.findViewById(R.id.jobDetail_requestDate);
        jobVisitDate = (TextView) _this.findViewById(R.id.jobDetail_visitDate);
        jobStartTime = (TextView) _this.findViewById(R.id.jobDetail_startTime);
        jobEndTime = (TextView) _this.findViewById(R.id.jobDetail_endTime);
        jobRemark = (TextView) _this.findViewById(R.id.jobDetail_remark);
        btn_photo = (Button) _this.findViewById(R.id.btn_photo);
    }


    public void setjobDetail() {
        Cursor cursor = DatabaseView.query("SELECT * FROM ServiceJob WHERE jobNo ='" + selectedjobNo + "'");
        cursor.moveToFirst();
        String _jobProblem = cursor.getString(cursor.getColumnIndex("jobProblem"));
        String _jobSerialNo = cursor.getString(cursor.getColumnIndex("serialNo"));
        String _jobRequestDate = cursor.getString(cursor.getColumnIndex("requestDate"));
        String _jobVisitDate = cursor.getString(cursor.getColumnIndex("visitDate"));
        String _jobStartTime = cursor.getString(cursor.getColumnIndex("jobStartTime"));
        String _jobEndTime = cursor.getString(cursor.getColumnIndex("jobEndTime"));
        String _jobRemark = cursor.getString(cursor.getColumnIndex("remark"));


        jobNo.setText(selectedjobNo);
        jobProblem.setText(_jobProblem);
        jobSerialNo.setText(_jobSerialNo);
        jobRequestDate.setText(_jobRequestDate);
        jobVisitDate.setText(_jobVisitDate);
        jobStartTime.setText(_jobStartTime);
        jobEndTime.setText(_jobEndTime);
        jobRemark.setText(_jobRemark);

        for (int i = 0; i < jobStatusItem.length; i++)
            if (jobStatusItem[i].equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("jobStatus"))))
                jobStatus.setSelection(i);

    }
}