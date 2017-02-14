package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 1/28/2017.
 */
public class ActivityAddNewOffice extends AppCompatActivity {
    EditText officeCode;
    Button addBtn;
    DatabaseAdapter database;
    int officeId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_new_office);
        Util.setStatusBarColor(ActivityAddNewOffice.this);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        initViews();
        eventListener();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        database = new DatabaseAdapter(ActivityAddNewOffice.this);
        officeCode = (EditText) findViewById(R.id.add_new_office_code);
        officeCode.setRawInputType(Configuration.KEYBOARD_QWERTY);
        addBtn = (Button) findViewById(R.id.add_new_office_btn);
    }

    private void eventListener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    officeId = Integer.valueOf(officeCode.getText().toString().trim());
                    addOffice();
                }
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
////        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//    }

    private boolean checkField() {
        if (officeCode.getText().toString().trim().isEmpty()) {
            new MessageBox(ActivityAddNewOffice.this, "لطفا کد آرایشگاه را وارد نمایید .").show();
            return false;
        }
        if (checkIfExistOfficeId(Integer.valueOf(officeCode.getText().toString()))) {
            new MessageBox(ActivityAddNewOffice.this, "کد آرایشگاه تکراری می باشد .").show();
            return false;
        }
        return true;
    }

    private boolean checkIfExistOfficeId(int officeId) {
        boolean exist = false;
        DatabaseAdapter database = new DatabaseAdapter(ActivityAddNewOffice.this);
        ArrayList<Office> offices = new ArrayList<Office>();
        try {
            if (database.openConnection()) {
                offices = database.getOfficeInfo();
            }
        } catch (MyException e) {
            new MessageBox(ActivityAddNewOffice.this, e.getMessage()).show();
        }
        for (Office of : offices) {
            if (of.getId() == officeId) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    private void addOffice() {

        final ProgressDialog dialog = ProgressDialog.show(ActivityAddNewOffice.this, "", "در حال ثبت آرایشگاه ...");
        dialog.getWindow().setGravity(Gravity.END);
        MyObservable.addOfficeForUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String result) {
                        if (result.toLowerCase().equals("ok")) {
                            getOfficeInfo(dialog);
                        } else {
                            dialog.dismiss();
                            new MessageBox(ActivityAddNewOffice.this, result).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(ActivityAddNewOffice.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getOfficeInfo(final ProgressDialog dialog) {

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
                        dialog.dismiss();
                        try {
                            if (database.openConnection()) {
                                setDefaultOffice(office);
                                G.officeInfo = office;
                                startActivity(new Intent(ActivityAddNewOffice.this, MainActivity.class));
                                finish();
                            }
                        } catch (MyException e) {
                            new MessageBox(ActivityAddNewOffice.this, e.getMessage()).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(ActivityAddNewOffice.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }

    private void setDefaultOffice(Office office) {
        try {
            if (database.openConnection()) {
                //pre office unset default
                if (G.officeInfo != null) {
                    G.officeInfo.setIsDefault(0);
                    database.updateOfficeInfo(G.officeInfo);
                }
                //new office set default
                office.setIsDefault(1);
                database.insertOfficeInfo2(office);
            }
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
