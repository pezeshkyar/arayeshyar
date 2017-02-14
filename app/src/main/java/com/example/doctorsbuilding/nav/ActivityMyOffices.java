package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
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
 * Created by hossein on 1/29/2017.
 */
public class ActivityMyOffices extends AppCompatActivity {

    private ImageButton backBtn;
    private Button addOffice;
    private TextView pageTitle;
    private ListView mListView;
    private CustomListViewMyOffices mAdapter;
    DatabaseAdapter database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_offices);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Util.setStatusBarColor(ActivityMyOffices.this);
        initViews();
        eventListener();
        getAllOffices();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        database = new DatabaseAdapter(ActivityMyOffices.this);
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("آرایشگاه من");
        addOffice = (Button) findViewById(R.id.my_offices_addbtn);
        mListView = (ListView) findViewById(R.id.my_offices_list);
        mAdapter = new CustomListViewMyOffices(ActivityMyOffices.this, new ArrayList<Office>());
        mListView.setAdapter(mAdapter);

    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMyOffices.this, ActivityAddNewOffice.class));
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, LviActionType actionType) {
                switch (actionType) {
                    case select:
                        switchOffice(position);
                        break;
                    case remove:
                        removeOffice(position);
                        break;
                }
            }
        });
    }

    private void getAllOffices() {
        MyObservable.getOfficeInfoFromPhone()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Office>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Office> offices) {
                        if (!offices.isEmpty()) {
                            mAdapter.addAll(offices);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void removeOffice(final int position) {

        MyObservable.deleteOfficeForUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), ((Office) mAdapter.getItem(position)).getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String result) {
                        if (!result.isEmpty()) {
                            if (result.toLowerCase().equals("ok")) {
                                try {
                                    if (database.openConnection()) {
                                        database.deleteOffice(((Office) mAdapter.getItem(position)).getId());
                                        mAdapter.remove(((Office) mAdapter.getItem(position)));
                                    }
                                } catch (MyException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                new MessageBox(ActivityMyOffices.this, result).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(ActivityMyOffices.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void switchOffice(int position) {
        final ProgressDialog dialog = ProgressDialog.show(ActivityMyOffices.this, "", "لطفا شکیبا باشید ...");
        dialog.getWindow().setGravity(Gravity.END);
        int officeId = ((Office) mAdapter.getItem(position)).getId();
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
                        if (office.getRole() != 0) {
                            try {
                                if (database.openConnection()) {
                                    //pre office unset default
                                    G.officeInfo.setIsDefault(0);
                                    database.updateOfficeInfo(G.officeInfo);
                                    //new office set default
                                    office.setIsDefault(1);
                                    database.updateOfficeInfo(office);
                                    //
                                    G.officeInfo = office;
                                }
                                Intent intent = new Intent(ActivityMyOffices.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(ActivityMyOffices.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }

}
