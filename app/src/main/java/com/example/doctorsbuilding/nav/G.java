package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.support.Ticket;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by hossein on 7/18/2016.
 */
public class G extends Application {
//    public static int officeId = -1;
    public static User UserInfo;
    public static Office officeInfo = null;
    public static Reservation reservationInfo = null;
    public static int resNum = -1;
    public static ArrayAdapter<Ticket> mAdapter;
    public static Context context;

    private static G instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static boolean isOnline() {
        if (instance == null) return false;
        ConnectivityManager cm =
                (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
