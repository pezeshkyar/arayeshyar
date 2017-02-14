package com.example.doctorsbuilding.nav.support;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MyException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class DiscussActivity extends Activity {
    private int ticketId;
    private DiscussArrayAdapter adapter;
    private ListView mListView;
    private EditText mEditText;
    private ImageButton mBtnSend;
    ImageButton backBtn;
    TextView pageTitle;
    PersianCalendar persianCalendar = new PersianCalendar();
    MessageSender messageSender;
    MessageReciever messageReciever;

    @Override
    protected void onPause() {
        super.onPause();
        if (messageReciever != null)
            messageReciever.cancel(true);
        if (messageSender != null)
            messageSender.cancel(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        Util.setStatusBarColor(DiscussActivity.this);

        ticketId = this.getIntent().getIntExtra("id", 0);

        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("پشتیبانی");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        mListView = (ListView) findViewById(R.id.support_listview);
        mBtnSend = (ImageButton) findViewById(R.id.support_btn_send);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
        mListView.setAdapter(adapter);
        mEditText = (EditText) findViewById(R.id.support_edit_text);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                adapter.add(new OneComment(false, mEditText.getText().toString()
//                        , persianCalendar.getPersianLongDateAndTime(), G.UserInfo.getFirstName() + " " + G.UserInfo.getLastName()));
                if (!mEditText.getText().toString().trim().isEmpty()) {
                    messageSender = new MessageSender();
                    messageSender.execute();

                    mEditText.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception ex) {
                    }
                    mListView.setSelection(mListView.getChildCount());

                } else {
                    new MessageBox(DiscussActivity.this, "لطفا متن درخواست را وارد نمایید .").show();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addItems();
    }


    private void addItems() {
        messageReciever = new MessageReciever();
        messageReciever.execute();
    }

    class MessageSender extends AsyncTask<String, Void, String> {
        String msg;
        String errMsg;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DiscussActivity.this, "", "در حال ارسال پیام ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            mBtnSend.setClickable(false);
            msg = mEditText.getText().toString().trim();
        }

        @Override
        protected String doInBackground(String... strings) {
            String res = null;
            try {
                res = WebService.invokeSetUserTicketMessageWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), ticketId, msg);
            } catch (MyException e) {
                errMsg = e.getMessage();
                return null;
            } catch (Throwable t) {
                res = null;
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mBtnSend.setClickable(true);
            if (errMsg != null) {
                dialog.dismiss();
                new MessageBox(DiscussActivity.this, msg).show();
            } else {
                if (result != null && !result.toUpperCase().equals("ERROR")) {
                    adapter.add(new OneComment(false, msg, result, G.UserInfo.getFirstName() + " " + G.UserInfo.getLastName()));
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(DiscussActivity.this, "خطایی در ارسال پیام رخ داده است .").show();
                }
            }
        }
    }

    class MessageReciever extends AsyncTask<String, String, Boolean> {
        ArrayList<Message> messages;
        ProgressDialog dialog;
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DiscussActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            mBtnSend.setClickable(false);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean res = true;
            try {
                messages = WebService.invokeGetUserTicketMessageWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), ticketId);
            } catch (MyException e) {
                res = false;
                msg = e.getMessage();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mBtnSend.setClickable(true);
            if (msg != null) {
                new MessageBox(DiscussActivity.this, msg).show();
            } else {
                for (Message m : messages) {
                    boolean isLeft = m.getUsername().equals(G.UserInfo.getUserName()) ? false : true;
                    adapter.add(new OneComment(isLeft, m.getMessage(), m.getDate(), m.getFirstName() + " " + m.getLastName()));
                }
            }
            dialog.dismiss();
        }
    }
}