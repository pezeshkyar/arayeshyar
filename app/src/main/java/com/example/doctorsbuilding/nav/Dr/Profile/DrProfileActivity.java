package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatFragment;
import com.example.doctorsbuilding.nav.Dr.Notification.NotificationFragment;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MyException;
import com.example.doctorsbuilding.nav.MyObservable;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Reservation;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DrProfileActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ImageButton backBtn;
    private ViewPager mViewPager;
    private CircleImageView profileImage;
    private DatabaseAdapter database;
    TabLayout tabLayout;
    final static int DR_PIC_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Util.setStatusBarColor(DrProfileActivity.this);
        setContentView(R.layout.activity_dr_profile);
        database = new DatabaseAdapter(DrProfileActivity.this);

        initViews();
        eventListeners();
//        getDoctorPhoto();
        profileImage.setImageBitmap(G.officeInfo.getPhoto());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {

        backBtn = (ImageButton) findViewById(R.id.dr_profile_backBtn);
        profileImage = (CircleImageView) findViewById(R.id.drProfile_btnDrPhoto);

        if (G.officeInfo.getPhoto() == null) {

            int id = R.drawable.doctor;
            try {
                if (database.openConnection()) {
                    G.officeInfo.setPhoto(database.getImageProfile(DR_PIC_ID));
                }
            } catch (MyException e) {
                new MessageBox(DrProfileActivity.this, e.getMessage()).show();
            }
            if (G.officeInfo.getPhoto() == null) {
                G.officeInfo.setPhoto(BitmapFactory.decodeResource(getBaseContext().getResources(), id));
            }


        }
        profileImage.setImageBitmap(G.officeInfo.getPhoto());

        TextView drName = (TextView) findViewById(R.id.tv_doctorName);
        TextView drExpert = (TextView) findViewById(R.id.tv_doctorInfo);
        drName.setTypeface(Util.getBoldFont());
        drExpert.setTypeface(Util.getBoldFont());
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
                    ((TextView) tabViewChild).setTypeface(Util.getNormalFont());
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

//    private void getDoctorPhoto() {
//        MyObservable.getDoctorPic(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Bitmap>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Bitmap photo) {
//                        G.officeInfo.setPhoto(photo);
//                        profileImage.setImageBitmap(photo);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        new MessageBox(DrProfileActivity.this, e.getMessage()).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
}