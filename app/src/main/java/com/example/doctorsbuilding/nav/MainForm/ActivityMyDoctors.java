package com.example.doctorsbuilding.nav.MainForm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.ActivityNotificationDialog;
import com.example.doctorsbuilding.nav.ContactUs;
import com.example.doctorsbuilding.nav.CustomTypefaceSpan;
import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoActivity;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 12/6/2016.
 */
public class ActivityMyDoctors extends AppCompatActivity {
    TextView pageTitle;
    ImageButton backBtn;
    private FrameLayout frm_nothing;
    FloatingActionButton addButton;
    ListView doctorsListView;
    ArrayList<Office> doctors = new ArrayList<Office>();
    CustomDoctorsListAdapter adapter_doctors = null;
    DatabaseAdapter database = null;
    AsyncGetOfficeForUser task_getOffices = null;
    AsyncGetDoctorPic task_getDoctorPic = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityMyDoctors.this);
        setContentView(R.layout.activity_my_doctor);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        G.setStatusBarColor(ActivityMyDoctors.this);
        initViews();
        eventListener();

        adapter_doctors = new CustomDoctorsListAdapter(ActivityMyDoctors.this, new ArrayList<Office>());
        doctorsListView.setAdapter(adapter_doctors);

//        initActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readSharedPrefrence();
        initActivity();

    }

    private void readSharedPrefrence() {
        G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
        G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        G.UserInfo.setRole(G.getSharedPreferences().getInt("role", 0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getOffices != null) {
            task_getOffices.cancel(true);
        }
        if (task_getDoctorPic != null) {
            task_getDoctorPic.cancel(true);
        }
    }

    private void initActivity() {
        if (G.UserInfo == null)
            G.UserInfo = new User();
        G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
        G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
            setUserLayout();
        }
    }

    private void initViews() {
        database = new DatabaseAdapter(ActivityMyDoctors.this);
        frm_nothing = (FrameLayout) findViewById(R.id.my_doctor_nothing);
        doctorsListView = (ListView) findViewById(R.id.my_doctor_lv);
        addButton = (FloatingActionButton) findViewById(R.id.my_doctor_fab);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("آرایشگر من");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);

    }

    private void eventListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frm_nothing.setVisibility(View.GONE);
                if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    startActivity(new Intent(ActivityMyDoctors.this, ActivityAllDoctors.class));
                }
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

    private void setUserLayout() {
        addButton.setVisibility(View.GONE);
        doctorsListView.setEnabled(false);

        if (database.openConnection()) {
            doctors = database.getMyDoctorOffice();
            database.closeConnection();
        }
        if (doctors != null && doctors.size() > 0) {
            adapter_doctors.addAll(doctors);
        } else {
            task_getOffices = new AsyncGetOfficeForUser();
            task_getOffices.execute();
        }
        doctorsListView.setEnabled(true);
        addButton.setVisibility(View.VISIBLE);
    }

    private class AsyncGetOfficeForUser extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityMyDoctors.this, "", "لطفا شکیبا باشید ...");
            dialog.show();
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                doctors = WebService.invokeGetOfficeForUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
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
                new MessageBox(ActivityMyDoctors.this, msg).show();
            } else {
                dialog.dismiss();
                if (doctors != null && doctors.size() > 0) {
                    if (database.openConnection()) {
                        for (Office of : doctors) {
                            of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                            database.insertoffice(of);
                            adapter_doctors.add(of);
                        }
                        database.closeConnection();
                    }
                    for (int i = 0; i < doctors.size(); i++) {
                        task_getDoctorPic = new AsyncGetDoctorPic();
                        task_getDoctorPic.execute(String.valueOf(i));
                    }
                } else {
//                    new MessageBox(ActivityMyDoctors.this, "شما مشتری آرایشگری نمی باشید، جهت افزودن آرایشگر از دکمه افزودن استفاده نمایید.").show();
                    frm_nothing.setVisibility(View.VISIBLE);
                    addButton.bringToFront();
                }
            }
        }
    }

    private class AsyncGetDoctorPic extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap drpic = null;
        int position;
        ArrayList<Office> mOffices = new ArrayList<Office>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                position = Integer.valueOf(strings[0]);
                mOffices = doctors;
                drpic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), mOffices.get(position).getId());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityMyDoctors.this, msg).show();
            } else {
                if (drpic != null) {
                    if (database.openConnection()) {
                        Office office = mOffices.get(position);
                        office.setPhoto(drpic);
                        database.updateOffice(office.getId(), office);
                        adapter_doctors.update(position, office);
                        database.closeConnection();
                    }
                }
            }
        }
    }
}