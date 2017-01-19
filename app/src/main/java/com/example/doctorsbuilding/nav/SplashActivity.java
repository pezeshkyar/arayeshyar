package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 7/24/2016.
 */
public class SplashActivity extends AppCompatActivity {
    TextView splashTv;
    ProgressBar progressBar;
    public UserType menu = UserType.None;
    DatabaseAdapter database;
    AsyncCallGetData asyncCallGetData = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(SplashActivity.this);
        setContentView(R.layout.activity_splash);

        splashTv = (TextView) findViewById(R.id.splash_tv);
        progressBar = (ProgressBar) findViewById(R.id.splash_heart);
        progressBar.setVisibility(View.VISIBLE);
        loadData();
        database = new DatabaseAdapter(SplashActivity.this);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncCallGetData != null)
            asyncCallGetData.cancel(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPause();
        G.UserInfo.setRole(G.getSharedPreferences().getInt("role", 0));
        G.officeInfo = new Office();
        G.officeId = -1;

    }

    private void loadData() {
        splashTv.setText("در حال دریافت اطلاعات ...");
        asyncCallGetData = new AsyncCallGetData();
        asyncCallGetData.execute();
    }

    private class AsyncCallGetData extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap doctorPic = null;
        boolean result = true;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                G.UserInfo.setRole(WebService.invokeGetRoleInOfficeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId));
                if (G.UserInfo.getRole() != UserType.Assistant.ordinal()) {
                    doctorPic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                    G.officeInfo = WebService.invokeGetOfficeInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            } catch (Throwable ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int currentRole = G.getSharedPreferences().getInt("role", 0);
            if (msg != null) {
                new MessageBox(SplashActivity.this, msg).show();
            } else {

                if (G.UserInfo.getRole() == UserType.None.ordinal()) {
                    result = false;
                }
                if (G.UserInfo.getRole() != UserType.Assistant.ordinal()) {
                    if (G.officeInfo != null) {
                        if (doctorPic == null)
                            doctorPic = BitmapFactory.decodeResource(SplashActivity.this.getResources(), R.drawable.doctor);

                        G.officeInfo.setPhoto(doctorPic);
                        if (currentRole == G.UserInfo.getRole()) {
                            G.officeInfo.setMyOffice(true);
                        } else {
                            G.officeInfo.setMyOffice(false);
                        }
                        database = new DatabaseAdapter(SplashActivity.this);
                        if (database.openConnection()) {
                            database.updateOffice(G.officeId, G.officeInfo);
                            database.closeConnection();
                        }

                    } else {
                        result = false;
                    }
                    if (result) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }else {
                    startActivity(new Intent(SplashActivity.this, ActivityPatientAssistant.class));
                }
                finish();
            }
        }
    }
}
