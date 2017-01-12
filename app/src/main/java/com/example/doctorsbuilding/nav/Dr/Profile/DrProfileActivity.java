package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.ActivityImageShow;
import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatFragment;
import com.example.doctorsbuilding.nav.Dr.Notification.NotificationFragment;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Reservation;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DrProfileActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ImageButton backBtn;
    private ViewPager mViewPager;
    //    private ImageButton btnGallery;
    private CircleImageView profileImage;
    private DatabaseAdapter database;
    //    private AsyncGetDoctorPic getDoctorPic;
    TabLayout tabLayout;
    final static int DR_PIC_ID = 1;
    private asyncCallReserveForGuestWS reserveForGuestWS;
    private asyncCallReserveForMeWS reserveForMeWS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(DrProfileActivity.this);
        setContentView(R.layout.activity_dr_profile);
        database = new DatabaseAdapter(DrProfileActivity.this);

        initViews();
        eventListeners();
        profileImage.setImageBitmap(G.officeInfo.getPhoto());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {

        backBtn = (ImageButton) findViewById(R.id.dr_profile_backBtn);
        profileImage = (CircleImageView) findViewById(R.id.drProfile_btnDrPhoto);

        if (G.doctorImageProfile == null) {

            int id = R.drawable.doctor;
            if (database.openConnection()) {
                G.doctorImageProfile = database.getImageProfile(DR_PIC_ID);
            }
            if (G.doctorImageProfile == null) {
                G.doctorImageProfile = BitmapFactory.decodeResource(getBaseContext().getResources(), id);
            }


        }
        profileImage.setImageBitmap(G.doctorImageProfile);

        TextView drName = (TextView) findViewById(R.id.tv_doctorName);
        TextView drExpert = (TextView) findViewById(R.id.tv_doctorInfo);
        drName.setTypeface(G.getBoldFont());
        drExpert.setTypeface(G.getBoldFont());
        drName.setText(G.officeInfo.getFirstname() + " " + G.officeInfo.getLastname());
        drExpert.setText(G.officeInfo.getSubExpertName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(2);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        changeTabsFont();

    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(G.getNormalFont());
                }
            }
        }
    }

    private void eventListeners() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DrProfileActivity.this, ActivityImageShow.class));
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new NotificationFragment().newInstance(position + 1);
                    break;
                case 1:
                    frag = new PersonalInfoFragment().newInstance(position + 1);
                    break;
                case 2:
                    frag = new DrNobatFragment().newInstance(position + 1);

                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:

                    return getResources().getString(R.string.pageTitle_comments);
                case 1:
                    return getResources().getString(R.string.pageTitle_info);
                case 2:
                    return getResources().getString(R.string.pageTitle_nobat);
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (G.reservationInfo != null) {
//            if (G.reservationInfo.getOwner() == UserType.User) {
//                reserveForMeWS = new asyncCallReserveForMeWS();
//                reserveForMeWS.execute();
//            } else if (G.reservationInfo.getOwner() == UserType.Guest) {
//                reserveForGuestWS = new asyncCallReserveForGuestWS();
//                reserveForGuestWS.execute();
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (reserveForGuestWS != null)
            reserveForGuestWS.cancel(true);
        if (reserveForMeWS != null)
            reserveForMeWS.cancel(true);
    }

    private class asyncCallReserveForMeWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DrProfileActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = G.reservationInfo;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeReserveForMeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum);
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
                new MessageBox(DrProfileActivity.this, msg).show();
            } else {
                if (result > 0) {
//                    resevationId = result;
                    dialog.dismiss();
                    Toast.makeText(DrProfileActivity.this, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new MessageBox(DrProfileActivity.this, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }

    private class asyncCallReserveForGuestWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DrProfileActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = G.reservationInfo;
        }

        @Override
        protected Void doInBackground(String... strings) {
            reservation.setPatientFirstName(strings[0]);
            reservation.setPatientLastName(strings[1]);
            reservation.setPatientPhoneNo(strings[2]);
            try {
                result = WebService.invokeReserveForGuestWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.UserInfo.getCityID());
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
                new MessageBox(DrProfileActivity.this, msg).show();
            } else {
                if (result > 0) {
//                    resevationId = result;
                    dialog.dismiss();
                    Toast.makeText(DrProfileActivity.this, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new MessageBox(DrProfileActivity.this, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }
}