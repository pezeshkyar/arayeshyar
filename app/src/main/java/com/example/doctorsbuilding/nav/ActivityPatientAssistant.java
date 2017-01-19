package com.example.doctorsbuilding.nav;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Util.CustomDatePickerDialog;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 1/18/2017.
 */
public class ActivityPatientAssistant extends AppCompatActivity {

    private TextView date;
    private TextView pageTitle;
    private ImageButton backBtn;
    private ListView mListView;
    private CustomListViewAssistantPatient adapter_patients;
    private asyncCallGetPatientList getPatientList;
    private asyncCallGetPatientListToday getPatientListToday;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityPatientAssistant.this);
        setContentView(R.layout.patientlist_for_assistant);
        initViews();
        eventListener();
        getPatientListToday = new asyncCallGetPatientListToday();
        getPatientListToday.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getPatientList != null)
            getPatientList.cancel(true);
        if(getPatientListToday != null)
            getPatientListToday.cancel(true);
    }

    private void initViews() {
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("لیست مشتریان");
        backBtn = (ImageButton)findViewById(R.id.toolbar_backBtn);
        date = (TextView)findViewById(R.id.patientlist_assistant_date);
        mListView = (ListView)findViewById(R.id.patientlist_assistant_lv);
        adapter_patients = new CustomListViewAssistantPatient(ActivityPatientAssistant.this, new ArrayList<PatientAssistant>());
        mListView.setAdapter(adapter_patients);
    }

    private void eventListener() {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(ActivityPatientAssistant.this);
                datePicker.show();
                datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                            date.setText(datePicker.getDate());
                            getPatientList = new asyncCallGetPatientList(datePicker.getShortDate());
                            getPatientList.execute();

                        }
                    }
                });
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class asyncCallGetPatientList extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        String date;
        ArrayList<PatientAssistant> patientInfos = null;

        public asyncCallGetPatientList(String date){
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityPatientAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                patientInfos = WebService.invokeGetPatientAssistantWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, date);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityPatientAssistant.this, msg).show();
            } else {
                dialog.dismiss();
                if (patientInfos != null && patientInfos.size() > 0) {
                    adapter_patients.clear();
                    adapter_patients.addAll(patientInfos);

                } else {
                    Toast.makeText(ActivityPatientAssistant.this, "مشتری برای پذیرش وجود ندارد .",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class asyncCallGetPatientListToday extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<PatientAssistant> patientInfos = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityPatientAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                patientInfos = WebService.invokeGetTodayPatientAssistantWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityPatientAssistant.this, msg).show();
            } else {
                dialog.dismiss();
                if (patientInfos != null && patientInfos.size() > 0) {
                    adapter_patients.clear();
                    adapter_patients.addAll(patientInfos);

                } else {
                    Toast.makeText(ActivityPatientAssistant.this, "مشتری برای پذیرش وجود ندارد .",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
