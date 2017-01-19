package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Util.CustomTimePickerDialog;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 1/16/2017.
 */
public class ActivityAssistant extends AppCompatActivity {

    private RelativeLayout toolbar;
    private TextView pageTitle;
    private TextView fromTime;
    private TextView toTime;
    private ImageButton backBtn;
    private EditText username;
    private Spinner spinner_taskes;
    private Button btn_insertAssistant;
    private Button btn_backToPage1;
    private Button btn_addTime;
    private CheckBox chx0;
    private CheckBox chx1;
    private CheckBox chx2;
    private CheckBox chx3;
    private CheckBox chx4;
    private CheckBox chx5;
    private CheckBox chx6;
    private ListView lv_assistants;
    private ListView lv_timing;
    private ViewFlipper mViewFliper;
    private CustomListViewAssitants adapter_assitants;
    private CustomListViewAssistantsTiming adapter_timing;
    private ArrayAdapter<TaskGroup> taskGroup_adapter;
    private asyncCallGetTaskGroups getTaskGroups = null;
    private asyncSetAssistant setAssistant = null;
    private asyncGetAssistants getAssistants = null;
    private asyncRemoveAssistant removeAssitant = null;
    private asyncRemoveAssistantTime removeAssistantTime = null;
    private asyncGetAssistantTime getAssistantTimes = null;
    int hour, min, duration;
    private final int PAGE1 = 0;
    private final int PAGE2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityAssistant.this);
        setContentView(R.layout.assistant_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
        getTaskGroups = new asyncCallGetTaskGroups();
        getTaskGroups.execute();
        getAssistants = new asyncGetAssistants();
        getAssistants.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getTaskGroups != null)
            getTaskGroups.cancel(true);
        if (setAssistant != null)
            setAssistant.cancel(true);
        if (getAssistants != null)
            getAssistants.cancel(true);
        if (removeAssitant != null)
            removeAssitant.cancel(true);
        if (removeAssistantTime != null)
            removeAssistantTime.cancel(true);
        if (getAssistantTimes != null)
            getAssistantTimes.cancel(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {

        toolbar = (RelativeLayout) findViewById(R.id.assistant_toolbar);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("مدیریت آرایشگر");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        username = (EditText) findViewById(R.id.assistant_meliCode);
        spinner_taskes = (Spinner) findViewById(R.id.assistant_task);
        taskGroup_adapter = new ArrayAdapter<TaskGroup>(ActivityAssistant.this, R.layout.spinner_item);
        spinner_taskes.setAdapter(taskGroup_adapter);
        btn_insertAssistant = (Button) findViewById(R.id.assistant_addBtn1);
        btn_backToPage1 = (Button) findViewById(R.id.assistant_btnBack);
        ///////////////////////////////////////////////////////////////////
        lv_assistants = (ListView) findViewById(R.id.assistant_lv1);
        adapter_assitants = new CustomListViewAssitants(ActivityAssistant.this, new ArrayList<Assistant>());
        lv_assistants.setAdapter(adapter_assitants);
        //////////////////////////////////////////////////////////////////
        lv_timing = (ListView) findViewById(R.id.assistant_lv2);
        adapter_timing = new CustomListViewAssistantsTiming(ActivityAssistant.this, new ArrayList<AssistantTiming>());
        lv_timing.setAdapter(adapter_timing);
        //////////////////////////////////////////////////////////////////
        mViewFliper = (ViewFlipper) findViewById(R.id.assistant_viewFlipper);
        fromTime = (TextView) findViewById(R.id.assistant_startTime);
        toTime = (TextView) findViewById(R.id.assistant_endTime);
        btn_addTime = (Button) findViewById(R.id.assistant_insertTimeTable);
        chx0 = (CheckBox) findViewById(R.id.assistant_chx0);
        chx1 = (CheckBox) findViewById(R.id.assistant_chx1);
        chx2 = (CheckBox) findViewById(R.id.assistant_chx2);
        chx3 = (CheckBox) findViewById(R.id.assistant_chx3);
        chx4 = (CheckBox) findViewById(R.id.assistant_chx4);
        chx5 = (CheckBox) findViewById(R.id.assistant_chx5);
        chx6 = (CheckBox) findViewById(R.id.assistant_chx6);
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_insertAssistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldPage1()) {
                    setAssistant = new asyncSetAssistant();
                    setAssistant.execute();
                }
            }
        });
        btn_backToPage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.VISIBLE);
                mViewFliper.setDisplayedChild(PAGE1);
                adapter_timing.clear();
                chx0.setChecked(false);
                chx1.setChecked(false);
                chx2.setChecked(false);
                chx3.setChecked(false);
                chx4.setChecked(false);
                chx5.setChecked(false);
                chx6.setChecked(false);
                fromTime.setText("");
                toTime.setText("");

            }
        });
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomTimePickerDialog datePicker = new CustomTimePickerDialog(ActivityAssistant.this);
                datePicker.show();
                datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                            fromTime.setText(datePicker.getTime());
                        }
                    }
                });
            }
        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomTimePickerDialog datePicker = new CustomTimePickerDialog(ActivityAssistant.this);
                datePicker.show();
                datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                            toTime.setText(datePicker.getTime());
                        }
                    }
                });
            }
        });
        btn_addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldPage2()) {
                    asyncSetAssistantTiming task = new asyncSetAssistantTiming();
                    task.execute();
                }
            }
        });
        adapter_assitants.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, LviActionType actionType) {
                switch (actionType) {
                    case select:
                        username.setText(((Assistant) adapter_assitants.getItem(position)).getUsername());
                        spinner_taskes.setSelection(getTaskPosition(position));
                        getAssistantTimes = new asyncGetAssistantTime(position);
                        getAssistantTimes.execute();
                        break;
                    case remove:
                        removeAssitant = new asyncRemoveAssistant(position);
                        removeAssitant.execute();
                        break;
                }
            }
        });

        adapter_timing.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, LviActionType actionType) {
                switch (actionType) {
                    case remove:
                        removeAssistantTime = new asyncRemoveAssistantTime(position);
                        removeAssistantTime.execute();
                }
            }
        });

    }

    private boolean checkFieldPage2() {

        if (fromTime.getText().toString().trim().isEmpty()) {
            new MessageBox(ActivityAssistant.this, "زمان شروع مشخص نشده است .").show();
            return false;
        }
        if (toTime.getText().toString().trim().isEmpty()) {
            new MessageBox(ActivityAssistant.this, "زمان پایان مشخص نشده است .").show();
            return false;
        }
        if (!isEndTimeGreaterThanStartTime()) {
            new MessageBox(ActivityAssistant.this, "زمان پایان از زمان شروع باید بیشتر باشد .").show();
            return false;
        }
        if (taskGroup_adapter.getCount() == 0) {
            new MessageBox(ActivityAssistant.this, "هیچ خدماتی برای آرایشگاه شما تعریف نشده است .").show();
            return false;
        }
        return true;
    }

    private boolean checkFieldPage1() {

        if (username.getText().toString().trim().isEmpty()) {
            new MessageBox(ActivityAssistant.this, "لطفا کد ملی را وارد نمایید .").show();
            return false;
        }
        if (!Util.IsValidCodeMeli(username.getText().toString().trim())) {
            new MessageBox(ActivityAssistant.this, "کد ملی وارد شده نادرست می باشد .").show();
            return false;
        }
        return true;
    }

    private boolean isEndTimeGreaterThanStartTime() {
        String start = fromTime.getText().toString().trim();
        String end = toTime.getText().toString().trim();

        String[] startTime = start.split(":");
        String[] endTime = end.split(":");

        int startHour = Integer.parseInt(startTime[0]);
        int startMinute = Integer.parseInt(startTime[1]);

        int endHour = Integer.parseInt(endTime[0]);
        int endMinute = Integer.parseInt(endTime[1]);

        if (startHour > endHour) {
            return false;
        } else if (startHour == endHour) {
            if (startMinute > endMinute) {
                return false;
            }
        }
        hour = startHour;
        min = startMinute;
        duration = ((endHour - startHour) * 60) + ((endMinute - startMinute));
        return true;
    }

    private int getTaskPosition(int index) {
        int position = -1;
        int taskGroupId = ((Assistant) adapter_assitants.getItem(index)).getTaskGroupId();
        for (int i = 0; i < taskGroup_adapter.getCount(); i++) {
            if (taskGroup_adapter.getItem(i).getId() == taskGroupId)
                position = i;
        }
        return position;
    }

    private class asyncCallGetTaskGroups extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<TaskGroup> taskGroups = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskGroups = WebService.invokeGetTaskGroupsWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (taskGroups != null && taskGroups.size() != 0) {
                    taskGroup_adapter.addAll(taskGroups);
                }
                dialog.dismiss();
            }
        }
    }

    private class asyncGetAssistants extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<Assistant> assistants = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                assistants = WebService.getAssistantOfficeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (assistants != null && assistants.size() != 0) {
                    adapter_assitants.addAll(assistants);
                }
                dialog.dismiss();
            }
        }
    }


    private class asyncSetAssistant extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        Assistant assistant = null;
        String assistantUsername = "";
        int taskGroupId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            assistantUsername = username.getText().toString().trim();
            taskGroupId = taskGroup_adapter.getItem(spinner_taskes.getSelectedItemPosition()).getId();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                assistant = WebService.setAssistantWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, assistantUsername, taskGroupId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (assistant != null) {
                    if (assistant.getErrorCode() != -1) {
                        adapter_assitants.add(assistant);
                        toolbar.setVisibility(View.GONE);
                        mViewFliper.setDisplayedChild(PAGE2);
                    } else {
                        new MessageBox(ActivityAssistant.this, "کد ملی وارد شده مجاز نمی باشد .").show();
                    }
                }
                dialog.dismiss();
            }
        }
    }

    private class asyncSetAssistantTiming extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<Integer> timingIds = null;
        String dayOfWeek;
        String assistantUsername = "";
        int taskGroupId;
        AssistantTiming timing = new AssistantTiming();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            assistantUsername = username.getText().toString().trim();
            taskGroupId = taskGroup_adapter.getItem(spinner_taskes.getSelectedItemPosition()).getId();
            dayOfWeek = (chx0.isChecked()) ? "0" : "";
            dayOfWeek += (chx1.isChecked()) ? "1" : "";
            dayOfWeek += (chx2.isChecked()) ? "2" : "";
            dayOfWeek += (chx3.isChecked()) ? "3" : "";
            dayOfWeek += (chx4.isChecked()) ? "4" : "";
            dayOfWeek += (chx5.isChecked()) ? "5" : "";
            dayOfWeek += (chx6.isChecked()) ? "6" : "";
            timing.setTime(fromTime.getText().toString().trim(), toTime.getText().toString().trim());

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                timingIds = WebService.setAssistantTimeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, assistantUsername, taskGroupId, dayOfWeek, timing.getHour(), timing.getMinute(), timing.getDuration());
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (timingIds != null && timingIds.size() != 0) {
                    AssistantTiming timeTable;
                    char[] days = dayOfWeek.toCharArray();
                    for (int i = 0; i < timingIds.size(); i++) {
                        timeTable = new AssistantTiming();
                        timeTable.setId(timingIds.get(i));
                        timeTable.setDate(days[i]);
                        timeTable.setHour(timing.getHour());
                        timeTable.setMinute(timing.getMinute());
                        timeTable.setDuration(timing.getDuration());
                        adapter_timing.add(timeTable);
                    }
                }
                dialog.dismiss();
            }
        }
    }

    private class asyncRemoveAssistant extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        boolean result = false;
        int position = -1;
        int assistanId = -1;

        public asyncRemoveAssistant(int position) {
            this.position = position;
            this.assistanId = ((Assistant) adapter_assitants.getItem(position)).getId();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.deleteAssistantWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, assistanId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (result) {
                    adapter_assitants.remove(position);
                } else {
                    new MessageBox(ActivityAssistant.this, "خطایی در عملیات حذف رخ داده است .").show();
                }
            }
            dialog.dismiss();
        }
    }

    private class asyncRemoveAssistantTime extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        boolean result = false;
        int position = -1;
        int timingId = -1;

        public asyncRemoveAssistantTime(int position) {
            this.position = position;
            this.timingId = ((AssistantTiming) adapter_timing.getItem(position)).getId();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.removeAssistantTimeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, timingId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (result) {
                    adapter_timing.remove(position);
                } else {
                    new MessageBox(ActivityAssistant.this, "خطایی در عملیات حذف رخ داده است .").show();
                }
            }
            dialog.dismiss();
        }
    }

    private class asyncGetAssistantTime extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<AssistantTiming> timings = null;
        int position = -1;
        int assistantId = -1;

        public asyncGetAssistantTime(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAssistant.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            assistantId = ((Assistant) adapter_assitants.getItem(position)).getId();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                timings = WebService.getAssistantTimeListWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, assistantId);
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
                new MessageBox(ActivityAssistant.this, msg).show();
            } else {
                if (timings != null && timings.size() != 0) {
                    adapter_timing.clear();
                    adapter_timing.addAll(timings);
                }
                toolbar.setVisibility(View.GONE);
                mViewFliper.setDisplayedChild(PAGE2);
                dialog.dismiss();
            }
        }
    }

}
