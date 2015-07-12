package com.tyict.ptms.JobService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tyict.ptms.A_Entry;
import com.tyict.ptms.NonStoppable;
import com.tyict.ptms.Other.F_ProductIssues;
import com.tyict.ptms.Other.F_CompanyDetails;
import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class F_JobDetail extends Fragment implements View.OnLongClickListener, View.OnClickListener
{
    View _this;
    private Bundle bundle;
    private String selectedjobNo;
    private TextView jobCompany, productName, jobNo, jobProblem, jobSerialNo, jobRequestDate, jobVisitDate, jobStartTime, jobEndTime, jobRemark;
    private Spinner jobStatus;
    private String[] jobStatusItem = {"completed", "follow-up", "pending", "cancelled", "postponed"};
    private Uri fileURI;
    private Button btn_photo, btn_startTimer, btn_cancelJob, btn_postpone;
    private ImageButton btn_viewImage, btn_viewSign;
    private AlertDialog.Builder editDialog;
    private ImageView image_preview;
    private static BackGroundTimer bgTimer;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private SignView _signView;

    @Override
    public boolean onLongClick(View view)
    {
        final EditText et = new EditText(getActivity());
        final View tmp = view;
        final String column;
        final String jobNoText = jobNo.getText().toString();
        int id = view.getId();
        et.setText(((TextView) view).getText().toString());
        editDialog.setView(et);
        if (id == jobProblem.getId())
        {
            column = "jobProblem";
        } else
        {
            column = "remark";
        }

        editDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                ((TextView) tmp).setText(et.getText().toString());
                DatabaseView.exec("UPDATE ServiceJob SET " + column + " = '" + et.getText().toString() + "' WHERE jobNo = '" + jobNoText + "'");
            }
        });
        editDialog.show();
        return true;
    }


    private static File getOutputMediaFile(int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else
        {
            return null;
        }
        return mediaFile;
    }

    private void disableEdit()
    {
        jobStatus.setEnabled(false);
        btn_startTimer.setEnabled(false);
        btn_startTimer.setVisibility(View.GONE);
        btn_cancelJob.setEnabled(false);
        btn_cancelJob.setTextColor(Color.GRAY);
        btn_photo.setEnabled(false);
        btn_photo.setTextColor(Color.GRAY);
        btn_postpone.setEnabled(false);
        btn_postpone.setTextColor(Color.GRAY);
    }

    @Override
    public void onClick(View view)
    {

        if (view.getId() == productName.getId())
        {
            Bundle bundle = new Bundle();
            bundle.putString("selectedJobNo", productName.getText().toString());
            Fragment productIssue = new F_ProductIssues();
            productIssue.setArguments(bundle);
            ((A_Entry) getActivity()).transferTo(productIssue, true);
        } else if (view.getId() == jobCompany.getId())
        {
            Bundle bundle = new Bundle();
            bundle.putString("comName", jobCompany.getText().toString());
            Fragment companyDetails = new F_CompanyDetails();
            companyDetails.setArguments(bundle);
            ((A_Entry) getActivity()).transferTo(companyDetails, true);
        } else if (view.getId() == btn_photo.getId())
        {
            try
            {
                ((BitmapDrawable) image_preview.getDrawable()).getBitmap().recycle();
            } catch (NullPointerException e)
            {

            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //fileURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            fileURI = getImageUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI); // set the image file name
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (view.getId() == btn_postpone.getId())
        {
            for (int i = 0; i < jobStatusItem.length; i++)
                if (jobStatusItem[i].equals("postponed"))
                {
                    jobStatus.setSelection(i);
                    DatabaseView.exec("UPDATE ServiceJob SET jobStatus = 'postponed' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                    Toast.makeText(getActivity(), "Successful change the status to postpone!", Toast.LENGTH_SHORT).show();
                }
        } else if (view.getId() == btn_cancelJob.getId())
        {
            for (int i = 0; i < jobStatusItem.length; i++)
                if (jobStatusItem[i].equals("cancelled"))
                {
                    jobStatus.setSelection(i);
                    DatabaseView.exec("UPDATE ServiceJob SET jobStatus = 'cancelled' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                    Toast.makeText(getActivity(), "Successful change the status to cancel!", Toast.LENGTH_SHORT).show();
                }
        } else if (view.getId() == btn_viewImage.getId())
        {

            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
            ImageView iv = new ImageView(getActivity());
            Bitmap bm = convertJPG(getImageUri());
            if (bm == null)
                return;
            iv.setImageBitmap(bm);
            ab.setView(iv);
            ab.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                }
            });
            ab.show();
        } else if (view.getId() == btn_viewSign.getId())
        {
            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
            ImageView iv = new ImageView(getActivity());
            Bitmap bm = _signView.loadFromStorage(getActivity(), getSignViewFileName());
            if (bm == null)
                return;
            iv.setImageBitmap(bm);
            ab.setView(iv);
            ab.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                }
            });
            ab.show();
        }
    }


    public Bitmap convertJPG(Uri filename)
    {
        Bitmap bm = null;
        FileInputStream input = null;
        try
        {
            input = new FileInputStream(new File(filename.getPath()));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bm = BitmapFactory.decodeStream(input, null, options);

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (input != null)
                    input.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return bm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _this = inflater.inflate(R.layout.jobdetail_layout, container, false);
        initVariable();
        setStatusItems();
        setjobDetail();
        if (!jobStatus.getSelectedItem().toString().equals("pending"))
        {
            disableEdit();
        } else if (NonStoppable.startingJob != null && !NonStoppable.startingJob.equals(""))
        {
            if (NonStoppable.startingJob.equals(jobNo.getText().toString()))
            {
                btn_startTimer.setText("CLICK TO FINISH");
                btn_startTimer.setTextColor(Color.RED);
                btn_startTimer.setBackground(getActivity().getResources().getDrawable(R.drawable.styled_button_finish_job));
                setOnClickListenerForTimer();
            } else
            {
                disableEdit();
                btn_startTimer.setText("You started " + NonStoppable.startingJob);
                btn_startTimer.setVisibility(View.VISIBLE);
            }
        } else
        {
            setOnClickListenerForTimer();
        }
        image_preview = new ImageView(getActivity());
        btn_photo.setOnClickListener(this);
        btn_cancelJob.setOnClickListener(this);
        btn_postpone.setOnClickListener(this);
        btn_viewImage.setOnClickListener(this);
        btn_viewSign.setOnClickListener(this);
        editDialog = new AlertDialog.Builder(getActivity());
        editDialog.setTitle("Edit");
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        jobProblem.setOnLongClickListener(this);
        jobRemark.setOnLongClickListener(this);
        jobStatus.setEnabled(false);
        return _this;
    }

    private Uri getImageUri()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM", "jobNo_" + jobNo.getText().toString() + ".jpg");
        Uri imgUri;
        try
        {
            imgUri = Uri.fromFile(file);
        } catch (Exception e)
        {
            return null;
        }

        return imgUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data)
    {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == getActivity().RESULT_OK)
            {
                image_preview = new ImageView(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                Toast.makeText(getActivity(), "Successful take the photo!", Toast.LENGTH_SHORT).show();
                image_preview.setImageURI(fileURI);
                image_preview.invalidate();
                builder.setView(image_preview);
                builder.setTitle("Preview");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                }).show();


            } else if (resultCode == getActivity().RESULT_CANCELED)
            {
            } else
            {
                Toast.makeText(getActivity(), "Unsuccessful take the photo!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getSignViewFileName()
    {
        return "sv_job" + jobNo.getText().toString() + ".jpg";
    }

    private void setOnClickListenerForTimer()
    {
        btn_startTimer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Calendar date = Calendar.getInstance();
                formatter = new SimpleDateFormat("HH:mm");
                final String time = formatter.format(date.getTime()).toString();
                formatter = new SimpleDateFormat("dd/MM/yyyy");
                String vdate = formatter.format(date.getTime()).toString();

                if (NonStoppable.startingJob != null && !NonStoppable.startingJob.equals(""))
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(_signView);
                    try
                    {
                    ((ViewGroup)_signView.getParent()).removeAllViews();}
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    builder.setTitle("Sign ");
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            final AlertDialog.Builder ibuilder = new AlertDialog.Builder(getActivity());
                            ImageView iv = new ImageView(getActivity());
                            iv.setImageBitmap(((SignView) _signView).getBitmap());
                            ibuilder
                                    .setView(iv)
                                    .setTitle("Your Sign")
                                    .setPositiveButton("Complete", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            DatabaseView.exec("UPDATE ServiceJob SET jobEndTime = '" + time + "' , jobStatus = 'completed' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                                            btn_startTimer.setText("START");
                                            jobEndTime.setText(time);
                                            btn_startTimer.setEnabled(false);
                                            btn_startTimer.setTextColor(Color.GRAY);
                                            NonStoppable.startingJob = null;
                                            (_signView).saveToStorage(getSignViewFileName());
                                            NonStoppable.startingJob = "";
                                            bgTimer.cancel(true);
                                            ((A_Entry) getActivity()).transferTo(new F_Job_List_Bird_View(), false);

                                        }
                                    })
                                    .setNegativeButton("FollowUp", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            DatabaseView.exec("UPDATE ServiceJob SET jobEndTime = '" + time + "' , jobStatus = 'follow-up' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                                            btn_startTimer.setText("START");
                                            jobEndTime.setText(time);
                                            btn_startTimer.setEnabled(false);
                                            NonStoppable.startingJob = "";
                                            btn_startTimer.setText("This job is ended");
                                            btn_startTimer.setTextColor(Color.GRAY);
                                            NonStoppable.startingJob = null;
                                            ( _signView).saveToStorage(getSignViewFileName());
                                            bgTimer.cancel(true);
                                            ((A_Entry) getActivity()).transferTo(new F_Job_List_Bird_View(), false);
                                        }
                                    })
                                    .show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                        }
                    }).show();


                } else
                {
                    bgTimer = new BackGroundTimer();
                    bgTimer.execute();
                    jobStartTime.setText(time);
                    btn_startTimer.setTextColor(Color.RED);
                    btn_startTimer.setBackground(getResources().getDrawable(R.drawable.styled_button_finish_job));
                    btn_startTimer.setText("CLICK TO FINISH");
                    btn_postpone.setEnabled(false);
                    btn_cancelJob.setEnabled(false);
                    NonStoppable.startingJob = jobNo.getText().toString();
                    jobVisitDate.setText(vdate);
                    NonStoppable.startingJob = jobNo.getText().toString();
                    DatabaseView.exec("UPDATE ServiceJob SET jobStartTime = '" + time + "' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                    DatabaseView.exec("UPDATE ServiceJob SET visitDate = '" + vdate + "' WHERE jobNo ='" + jobNo.getText().toString() + "'");
                }
                formatter = new SimpleDateFormat("HH-mm-ss");
            }
        });
    }

    private void setStatusItems()
    {
        jobStatus.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, jobStatusItem));
        jobStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                DatabaseView.exec("UPDATE ServiceJob SET jobStatus " + " = '" + jobStatus.getSelectedItem().toString() + "' WHERE jobNo = '" + jobNo.getText().toString() + "'");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    private void initVariable()
    {
        bundle = getArguments();
        selectedjobNo = bundle.getString("selectedJobNo");
        jobCompany = (TextView) _this.findViewById(R.id.jobDetail_companyName);
        jobNo = (TextView) _this.findViewById(R.id.jobDetail_jobNo);
        _signView = new SignView(getActivity());
        jobProblem = (TextView) _this.findViewById(R.id.jobDetail_problem);
        jobStatus = (Spinner) _this.findViewById(R.id.jobDetail_status);
        jobSerialNo = (TextView) _this.findViewById(R.id.jobDetail_serialNo);
        jobRequestDate = (TextView) _this.findViewById(R.id.jobDetail_requestDate);
        jobVisitDate = (TextView) _this.findViewById(R.id.jobDetail_visitDate);
        jobStartTime = (TextView) _this.findViewById(R.id.jobDetail_startTime);
        productName = (TextView) _this.findViewById(R.id.jobDetail_product);
        jobEndTime = (TextView) _this.findViewById(R.id.jobDetail_endTime);
        jobRemark = (TextView) _this.findViewById(R.id.jobDetail_remark);
        btn_viewImage = (ImageButton) _this.findViewById(R.id.btn_viewPhoto);
        btn_viewSign = (ImageButton) _this.findViewById(R.id.btn_viewSign);
        btn_photo = (Button) _this.findViewById(R.id.btn_photo);
        btn_cancelJob = (Button) _this.findViewById(R.id.jobDetail_btnCancelJob);
        btn_postpone = (Button) _this.findViewById(R.id.jobDetail_btnPostpone);
        btn_startTimer = (Button) _this.findViewById(R.id.btn_startJob);
    }

    private String companyID, prodctID;

    public void setjobDetail()
    {
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
        jobCompany.setOnClickListener(this);
        productName.setText(cursor.getString(cursor.getColumnIndex("prodName")));
        productName.setOnClickListener(this);
        for (int i = 0; i < jobStatusItem.length; i++)
            if (jobStatusItem[i].equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("jobStatus"))))
                jobStatus.setSelection(i);

    }

    SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");

    private class BackGroundTimer extends AsyncTask<String, String, String>
    {

        private int notiID = 1;
        private Calendar time;
        private boolean stop = false;
        private NotificationCompat.Builder noti;
        NotificationManager nm;

        @Override
        protected void onPreExecute()
        {
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

        protected String doInBackground(String... strings)
        {

            while (!isCancelled())
            {
                try
                {
                    Thread.sleep(1000);
                    time.add(Calendar.SECOND, 1);
                    noti.setContentText("Starting Job " + jobNo.getText().toString() + " " + formatter.format(time.getTime()));
                    nm.notify(notiID, noti.build());
                } catch (Exception e)
                {

                }
            }
            nm.cancel(notiID);
            return null;
        }

    }
}


class Notification extends Activity
{
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        setContentView(tv);
    }

    public void setText(String s)
    {
        tv.setText(s);
    }

}