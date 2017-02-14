package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityLoading extends AppCompatActivity {
    public UserType menu = UserType.None;
    DatabaseAdapter database;
    String username, password;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_loading_layout);
        Util.setStatusBarColor(ActivityLoading.this);
        mPrefs = Util.getSharedPreferences(ActivityLoading.this);
        getUserInfo();
        database = new DatabaseAdapter(ActivityLoading.this);
        try {
            database.initialize();
        } catch (MyException e) {
            new MessageBox(ActivityLoading.this, e.getMessage()).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void getUserInfo() {

        username = mPrefs.getString("user", "");
        password = mPrefs.getString("pass", "");

        Observable<Integer> observable1 = MyObservable.getRoleInAll(username, password)
                .subscribeOn(Schedulers.newThread());
        Observable<User> observable2 = MyObservable.getUserInfoWithPic(username, password)
                .subscribeOn(Schedulers.newThread());

        observable1.zipWith(observable2, new BiFunction<Integer, User, User>() {
            @Override
            public User apply(Integer role, User user) throws Exception {
                user.setRole(role);
                return user;
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        UserType usr = UserType.values()[user.getRole()];
                        switch (usr) {
                            case None:
                                mPrefs.edit().remove("user").apply();
                                mPrefs.edit().remove("pass").apply();
                                mPrefs.edit().remove("role").apply();
                                Intent intent1 = new Intent(ActivityLoading.this, SignInActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                                finish();
                                break;
                            case Guest:
                                Intent intent2 = new Intent(ActivityLoading.this, UserProfileActivity.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent2);
                                finish();
                                break;
                            default:
                                G.UserInfo = user;
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putString("user", G.UserInfo.getUserName());
                                editor.putString("pass", G.UserInfo.getPassword());
                                editor.putInt("role", G.UserInfo.getRole());
                                editor.apply();
                                getOfficeInfoFromPhone();
                                break;
                        }

                    }

                    @Override
                    public void onError(Throwable error) {
                        new MessageBox(ActivityLoading.this, error.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void getOfficeInfoFromPhone() {

        MyObservable.getOfficeInfoFromPhone()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Office>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ArrayList<Office> offices) {
                        int officeId = -1;
                        if (offices.isEmpty()) {
                            getOfficeInfoFromWeb();
                        } else {
                            for (Office of : offices) {
                                if (of.getIsDefault() == 1) {
                                    officeId = of.getId();
                                    break;
                                }
                            }
                            if (officeId == -1)
                                officeId = offices.get(0).getId();
                            getDefaultOfficeData(officeId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(ActivityLoading.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getOfficeInfoFromWeb() {
        MyObservable.getOfficeInfoFromWeb(username, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Office>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Office> offices) {
                        if (offices.isEmpty()) {
                            Intent intent = new Intent(ActivityLoading.this, ActivityAddNewOffice.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                if (database.openConnection()) {
                                    offices.get(0).setIsDefault(1);
                                    database.insertOfficeInfo(offices);
                                    G.officeInfo = offices.get(0);
                                    startActivity(new Intent(ActivityLoading.this, MainActivity.class));
                                    finish();
                                }
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(ActivityLoading.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getDefaultOfficeData(final int officeId) {

        Observable<Office> observable1 = MyObservable.getOfficeInfoFromWebWithOfficeId(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId)
                .subscribeOn(Schedulers.newThread());
        Observable<Bitmap> observable2 = MyObservable.getDoctorPic(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId)
                .subscribeOn(Schedulers.newThread());
        Observable<Integer> observable3 = MyObservable.getRoleInOffice(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId)
                .subscribeOn(Schedulers.newThread());
        Observable.zip(observable1, observable2, observable3, new Function3<Office, Bitmap, Integer, Office>() {
            @Override
            public Office apply(Office office, Bitmap photo, Integer role) throws Exception {
                office.setPhoto(photo);
                office.setRole(role);
                return office;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Office>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Office office) {
                        if (office.getRole() != 0) {
                            try {
                                if (database.openConnection()) {
                                    database.updateOfficeInfo(office);
                                    G.officeInfo = office;
                                }
                                startActivity(new Intent(ActivityLoading.this, MainActivity.class));
                                finish();
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(ActivityLoading.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

}
