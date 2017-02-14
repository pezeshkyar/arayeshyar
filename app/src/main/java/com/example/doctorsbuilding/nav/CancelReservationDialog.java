package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserInboxActivity;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 8/1/2016.
 */
public class CancelReservationDialog extends Dialog {

    private Context context;
    private int turnId;
    private boolean cancleResult = false;
    private int selectedItem = -1;
    private ListView mListView;
    private TextView nothingTxt;
    private CustomReservationListAdapter reservationListAdapter;
    private ArrayList<Reservation> reservations;
    private ProgressBar progressBar;

    asyncCallGetReservationByTurnIdWS getReservationByTurnIdTask;
    asyncCallCancelReservationWS cancelReservationTask;

    public CancelReservationDialog(Context context, int turnId) {
        super(context);
        this.context = context;
        this.turnId = turnId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cancel_reservayion_layout);
        initViews();
        eventListener();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.cancelReservationListView);
        reservationListAdapter = new CustomReservationListAdapter(context, new ArrayList<User>());
        mListView.setAdapter(reservationListAdapter);
        progressBar = (ProgressBar) findViewById(R.id.cancelReservation_Progress);
        nothingTxt = (TextView) findViewById(R.id.cancelReservation_nothing);
        getReservationByTurnIdTask = new asyncCallGetReservationByTurnIdWS();
        getReservationByTurnIdTask.execute();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (getReservationByTurnIdTask != null) {
            getReservationByTurnIdTask.cancel(true);
        }
        if (cancelReservationTask != null) {
            cancelReservationTask.cancel(true);
        }
    }

    private void eventListener() {

        reservationListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position, LviActionType actionType) {
                if (actionType == LviActionType.select) {
                    final MessageBox message = new MessageBox(context, "شما در حال حذف این نوبت می باشید !");
                    message.show();

                    message.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (message.pressAcceptButton()) {
                                selectedItem = position;
                                cancelReservationTask = new asyncCallCancelReservationWS();
                                cancelReservationTask.execute();
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean getCancelationResult() {
        return cancleResult;
    }

    private class asyncCallGetReservationByTurnIdWS extends AsyncTask<String, Void, Void> {
        Reservation reservation = null;
        String msg = null;


        @Override
        protected Void doInBackground(String... strings) {
            try {
                reservations = WebService.invokeGetReservationByTurnIdWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), turnId);
            } catch (MyException ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(context, msg).show();
            } else {
                if (reservations.size() != 0) {
                    User userInfo = null;
                    ArrayList<User> users = new ArrayList<User>();
                    for (Reservation res : reservations) {
                        userInfo = new User();
                        userInfo.setFirstName(res.getPatientFirstName());
                        userInfo.setLastName(res.getPatientLastName());
                        userInfo.setPhone(res.getPatientPhoneNo());
                        users.add(userInfo);
                    }
                    reservationListAdapter.addAll(users);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    nothingTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class asyncCallCancelReservationWS extends AsyncTask<String, Void, Void> {
        String result = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال حذف نوبت ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            mListView.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeCancleReservation(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservations.get(selectedItem).getId());
            } catch (MyException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListView.setEnabled(true);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(context, msg).show();
            } else {
                if (result != null) {
                    dialog.dismiss();
                    if (result.toUpperCase().equals("OK")) {
                        cancleResult = true;
                        Toast.makeText(context, "حذف نوبت با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                    } else {
                        cancleResult = false;
                        new MessageBox(context, result).show();
                    }
                    dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "عملیات حذف با مشکل مواجه شد !").show();
                }
            }
        }
    }
}