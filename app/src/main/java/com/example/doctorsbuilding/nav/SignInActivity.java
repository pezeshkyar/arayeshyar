package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.MainForm.ActivityOffices;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/18/2016.
 */
public class SignInActivity extends AppCompatActivity {
    private ViewFlipper viewFlipper;
    private Button btnSendCode;
    private EditText txtUserName;
    private SharedPreferences settings;
    private String password;
    private EditText txtSmsCode;
    private Button btnLogin;
    private Button btnResendSms;
    AsyncCallLoginWS loginWS;
    AsyncVerifySecurityCodeWS verifySecurityCodeWS;
    SendActivationCodeWS sendActivationCodeWS;
    private static final int PAGE1 = 0;
    private static final int PAGE2 = 1;
    private static final int REQUEST_CODE = 1024;
    private static final int LOGIN_RESPONSE_CODE = 0;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);
        settings = G.getSharedPreferences();
        G.setStatusBarColor(SignInActivity.this);
        initViews();
        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginWS != null)
            loginWS.cancel(true);
        if (verifySecurityCodeWS != null)
            verifySecurityCodeWS.cancel(true);
        if (sendActivationCodeWS != null)
            sendActivationCodeWS.cancel(true);
    }

    private void initViews() {
        viewFlipper = (ViewFlipper) findViewById(R.id.login_viewFlipper);
        btnSendCode = (Button) viewFlipper.findViewById(R.id.login_btn_sendCode);
        txtUserName = (EditText) viewFlipper.findViewById(R.id.login_userName);
        txtUserName.setRawInputType(Configuration.KEYBOARD_QWERTY);
        txtSmsCode = (EditText) viewFlipper.findViewById(R.id.login_txt_smsCode);
        txtSmsCode.setRawInputType(Configuration.KEYBOARD_QWERTY);
        btnLogin = (Button) viewFlipper.findViewById(R.id.login_btn_login);
        btnResendSms = (Button) viewFlipper.findViewById(R.id.login_btn_resendSms);

    }

    private void eventListener() {

        btnResendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.getSharedPreferences().edit().remove("phone").apply();
                showPrevious();
                viewFlipper.setDisplayedChild(PAGE1);
            }
        });

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldSendSecurityCode()) {
                    sendActivationCodeWS = new SendActivationCodeWS();
                    sendActivationCodeWS.execute();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldVerifySecurityCode()) {
                    verifySecurityCodeWS = new AsyncVerifySecurityCodeWS();
                    verifySecurityCodeWS.execute();
                }
            }
        });

    }

    private void signIn() {
        if (checkField()) {
            loginWS = new AsyncCallLoginWS();
            loginWS.execute();
        }
    }

    private boolean checkFieldVerifySecurityCode() {
        if (txtSmsCode.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا رمزی که به تلفن همراه شما پیامک شده را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    private boolean checkFieldSendSecurityCode() {
        if (txtUserName.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا شماره تلفن همراه خود را وارد نمایید .").show();
            return false;
        }
        if (txtUserName.getText().toString().trim().length() != 11) {
            new MessageBox(SignInActivity.this, "شماره تلفن همراه وارد شده نادرست می باشد .").show();
            return false;
        }
        return true;
    }

    private boolean checkField() {
        if (txtUserName.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا شماره تلفن همراه را وارد نمایید.").show();
            return false;
        }
        return true;
    }

    private void showPrevious() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
//        viewFlipper.showNext();
    }

    private void showNext() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
//        viewFlipper.showPrevious();
    }

    private class AsyncCallLoginWS extends AsyncTask<String, Void, Void> {
        private int result = -1;
        private UserType userType = UserType.None;
        String msg = null;
        User user = null;
        Bitmap userPic = null;
        ProgressDialog dialog;
        String username;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            username = txtUserName.getText().toString().trim();
            password = txtSmsCode.getText().toString().trim();
            try {
                password = Hashing.SHA1(password);
            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeLogin3WS(username, password);
                if (result != 0) {
                    user = WebService.invokeGetUserInfoWS(username, password, G.officeId);
                    if (user != null) {
                        userPic = WebService.invokeGetUserPicWS(username, password);
                    }
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                if (result != -1) {
                    userType = UserType.values()[result];
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("menu", userType);
                    switch (userType) {
                        case None:
                            new MessageBox(SignInActivity.this, "شماره تلفن همراه یا کلمه عبور نادرست می باشد.").show();
                            break;
                        case User:
                            UserType.User.attachTo(intent);
                            save(result);
                            break;
                        case Dr:
                            UserType.Dr.attachTo(intent);
                            save(result);
                            break;
                        case secretary:
                            UserType.Dr.attachTo(intent);
                            save(result);
                            break;
                        case Assistant:
                            UserType.Assistant.attachTo(intent);
                            save(result);
                            break;
                        default:
                            break;

                    }
                }
                if (user != null) {
                    G.UserInfo = user;
                    if (userPic != null) {
                        G.UserInfo.setImgProfile(userPic);
                    } else {
                        G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                    }
                    setResult(LOGIN_RESPONSE_CODE);
                    finish();
                }
            }
        }

        private void save(int role) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("role", role);
            editor.apply();
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception ex) {
            }
        }
    }

    private class SendActivationCodeWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        String msg = null;
        ProgressDialog dialog;
        String phoneNumber;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnSendCode.setClickable(false);
            phoneNumber = txtUserName.getText().toString().trim();
            try {
                password = Hashing.SHA1(Util.getStringWS(R.string.updateCode));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.sendActivationCodeWS(phoneNumber, password);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                btnSendCode.setClickable(true);
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                btnSendCode.setClickable(true);
                if (result != null) {
                    if (result.toUpperCase().equals("OK")) {
                        Toast.makeText(SignInActivity.this, Util.getStringWS(R.string.siginACT_forget_msg), Toast.LENGTH_LONG).show();
                        //save phoneNumber
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("phone", phoneNumber);
                        editor.apply();
                        //show next layout
                        showNext();
                        viewFlipper.setDisplayedChild(PAGE2);

                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                } else {
                    new MessageBox(SignInActivity.this, "درخواست شما با مشکل مواجه شده است .").show();
                }
            }
        }
    }

    private class AsyncVerifySecurityCodeWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        String msg = null;
        ProgressDialog dialog;
        private String phoneNumber;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phoneNumber = G.getSharedPreferences().getString("phone", "");
            try {
                password = Hashing.SHA1(txtSmsCode.getText().toString().trim());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnLogin.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.verifySecurityCodeWS(phoneNumber, password);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                btnLogin.setClickable(true);
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                btnLogin.setClickable(true);
                if (result != null) {
                    if (result.toLowerCase().equals("signin")) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("user", phoneNumber);
                        editor.putString("pass", password);
                        editor.apply();
                        G.getSharedPreferences().edit().remove("phone").apply();
                        signIn();

                    } else if (result.toLowerCase().equals("signup")) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("user", phoneNumber);
                        editor.putString("pass", password);
                        editor.apply();
                        G.getSharedPreferences().edit().remove("phone").apply();
                        startActivityForResult(new Intent(SignInActivity.this, UserProfileActivity.class), REQUEST_CODE);
                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                } else {
                    new MessageBox(SignInActivity.this, "درخواست شما با مشکل مواجه شده است .").show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            setResult(resultCode);
            finish();
        }
    }
}