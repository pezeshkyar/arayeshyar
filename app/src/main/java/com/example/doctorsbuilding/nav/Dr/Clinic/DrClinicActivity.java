package com.example.doctorsbuilding.nav.Dr.Clinic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MyException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SubExpert;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 5/23/2016.
 */
public class DrClinicActivity extends AppCompatActivity {
    EditText secretary;
    EditText Address;
    EditText phone;
    EditText biography;
    Button insertBtn;
    Spinner state;
    Spinner city;
    Spinner expert;
    Spinner subExpert;
    ArrayList<City> cities;
    ArrayList<State> states;
    ArrayList<Expert> experts;
    ArrayList<SubExpert> subExperts;
    ArrayAdapter<State> stateAdapter;
    ArrayAdapter<City> cityAdapter;
    ArrayAdapter<Expert> expertAdapter;
    ArrayAdapter<SubExpert> subExpertAdapter;
    boolean isOfficeExist;
    int stateSelectedIndex = -1;
    int expertSelectedIndex = -1;
    ImageButton backBtn;
    TextView pageTitle;
    ProgressDialog dialog;

    AsyncCallStateWS getStateTask;
    AsyncCallGetExpertWS getExpertTask;
    AsyncCallCityWS getCityTask;
    AsyncCallSubExpertWS getSubExpertTask;
    AsyncCallUpdateOfficeWS updateOfficeTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Util.setStatusBarColor(DrClinicActivity.this);
        setContentView(R.layout.activity_dr_clinic);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListener();

        if (G.officeInfo != null) {
            isOfficeExist = true;
            showOfficeInfo();
        }
        dialog = ProgressDialog.show(DrClinicActivity.this, "", "در حال دریافت اطلاعات ...");
        dialog.getWindow().setGravity(Gravity.END);
        dialog.setCancelable(true);
        insertBtn.setClickable(false);

        getStateTask = new AsyncCallStateWS();
        getStateTask.execute();

        getExpertTask = new AsyncCallGetExpertWS();
        getExpertTask.execute();


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAllAsyncTask();
    }

    private void stopAllAsyncTask() {
        if (getStateTask != null)
            getStateTask.cancel(true);

        if (getExpertTask != null)
            getExpertTask.cancel(true);

        if (getCityTask != null)
            getCityTask.cancel(true);

        if (getSubExpertTask != null)
            getSubExpertTask.cancel(true);

        if (updateOfficeTask != null)
            updateOfficeTask.cancel(true);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("مشخصات آرایشگاه");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        state = (Spinner) findViewById(R.id.dr_office_state);
        city = (Spinner) findViewById(R.id.dr_office_city);
        Address = (EditText) findViewById(R.id.dr_office_address);
        phone = (EditText) findViewById(R.id.dr_office_phone);
        phone.setRawInputType(Configuration.KEYBOARD_QWERTY);
        expert = (Spinner) findViewById(R.id.dr_office_spec);
        subExpert = (Spinner) findViewById(R.id.dr_office_subSpec);
//        secretary = (EditText) findViewById(R.id.dr_office_secretary);
        biography = (EditText) findViewById(R.id.dr_office_biography);
        insertBtn = (Button) findViewById(R.id.dr_office_btnInsert);

    }

    private void eventListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrClinicActivity.this.onBackPressed();
            }
        });

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stateSelectedIndex = i;
                getCityTask = new AsyncCallCityWS();
                getCityTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        expert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expertSelectedIndex = i;
                getSubExpertTask = new AsyncCallSubExpertWS();
                getSubExpertTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    updateOfficeTask = new AsyncCallUpdateOfficeWS();
                    updateOfficeTask.execute();
                }
            }
        });

    }

    private void showOfficeInfo() {
        Address.setText(G.officeInfo.getAddress());
        phone.setText(G.officeInfo.getPhone());
        biography.setText(G.officeInfo.getBiography());

    }

    private boolean checkField() {
        if (state.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا استان خود را انتخاب نمایید .").show();
            return false;
        }
        if (city.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا شهر خود را انتخاب نمایید .").show();
            return false;
        }
        if (Address.getText().toString().trim().equals("")) {
            new MessageBox(this, "لطفا آدرس آرایشگاه را وارد نمایید .").show();
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            new MessageBox(this, "لطفا شماره تلفن آرایشگاه را وارد نمایید .").show();
            return false;
        }
        if (expert.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا تخصص خود را وارد نمایید .").show();
            return false;
        }
        if (subExpert.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا زیر تخصص خود را وارد نمایید .").show();
            return false;
        }
        return true;
    }
    //set state spinner ............................................................................

    private class AsyncCallStateWS extends AsyncTask<String, Void, Void> {

        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                states = WebService.invokeGetProvinceNameWS();
                if (isOfficeExist) {
                    cities = WebService.invokeGetCityNameWS(G.officeInfo.getStateId());
                } else {
                    cities = WebService.invokeGetCityNameWS(1);
                }
            } catch (MyException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                insertBtn.setClickable(true);
                new MessageBox(DrClinicActivity.this, msg).show();
            } else {

                setStateSpinner();
                if (isOfficeExist) {
                    for (int i = 0; i < states.size(); i++) {
                        if (states.get(i).GetStateID() == G.officeInfo.getStateId()) {
                            state.setSelection(i);
                            break;
                        }
                    }
                }
                setCitySpinner();
            }
        }

        private void setStateSpinner() {
            stateAdapter = new ArrayAdapter<State>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, states);
            state.setAdapter(stateAdapter);
        }

        private void setCitySpinner() {
            cityAdapter = new ArrayAdapter<City>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, cities);
            city.setAdapter(cityAdapter);
        }

    }

    // set city spinner

    private class AsyncCallCityWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (stateSelectedIndex != -1) {
                    cities = WebService.invokeGetCityNameWS(states.get(stateSelectedIndex).GetStateID());
                } else if (isOfficeExist) {
                    cities = WebService.invokeGetCityNameWS(G.officeInfo.getStateId());
                }
            } catch (MyException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                insertBtn.setClickable(true);
                new MessageBox(DrClinicActivity.this, msg).show();
            } else {
                setCitySpinner();

                if (isOfficeExist) {
                    for (int i = 0; i < DrClinicActivity.this.cities.size(); i++) {
                        if (DrClinicActivity.this.cities.get(i).GetCityID() == G.officeInfo.getCityId()) {
                            city.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }

        private void setCitySpinner() {
            cityAdapter = new ArrayAdapter<City>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, cities);
            city.setAdapter(cityAdapter);
        }

    }

    //set expert spinner ..........................................................................

    private class AsyncCallGetExpertWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                experts = WebService.invokeGetSpecWS();
                if (isOfficeExist) {
                    subExperts = WebService.invokeGetSubSpecWS(G.officeInfo.getExpertId());
                } else {
                    subExperts = WebService.invokeGetSubSpecWS(1);
                }
            } catch (MyException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                insertBtn.setClickable(true);
                new MessageBox(DrClinicActivity.this, msg).show();
            } else {
                setExpertSpinner();
                if (isOfficeExist) {
                    for (int i = 0; i < experts.size(); i++) {
                        if (experts.get(i).getId() == G.officeInfo.getExpertId()) {
                            expert.setSelection(i);
                            break;
                        }
                    }
                }
                setSubExpertSpinner();
            }
        }

        private void setExpertSpinner() {
            expertAdapter = new ArrayAdapter<Expert>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, experts);
            expert.setAdapter(expertAdapter);
        }

        private void setSubExpertSpinner() {
            subExpertAdapter = new ArrayAdapter<SubExpert>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, subExperts);
            subExpert.setAdapter(subExpertAdapter);
        }

    }

    //set sub expert  ..............................................................

    private class AsyncCallSubExpertWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (expertSelectedIndex != -1) {
                try {
                    subExperts = WebService.invokeGetSubSpecWS(experts.get(expertSelectedIndex).getId());
                } catch (MyException ex) {
                    msg = ex.getMessage();
                }
            } else if (isOfficeExist) {
                try {
                    subExperts = WebService.invokeGetSubSpecWS(G.officeInfo.getExpertId());
                } catch (MyException ex) {
                    msg = ex.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                insertBtn.setClickable(true);
                new MessageBox(DrClinicActivity.this, msg).show();
            } else {
                setSubExpertSpinner();

                if (isOfficeExist) {
                    for (int i = 0; i < DrClinicActivity.this.subExperts.size(); i++) {
                        if (DrClinicActivity.this.subExperts.get(i).getId() == G.officeInfo.getSubExpertId()) {
                            subExpert.setSelection(i);
                            break;
                        }
                    }
                }
                dialog.dismiss();
                insertBtn.setClickable(true);
            }
        }

        private void setSubExpertSpinner() {
            subExpertAdapter = new ArrayAdapter<SubExpert>(DrClinicActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, subExperts);
            subExpert.setAdapter(subExpertAdapter);
        }

    }

    //update office info ..........................................................................

    private class AsyncCallUpdateOfficeWS extends AsyncTask<String, Void, Void> {
        boolean result = false;
        Office office;
        String msg = null;
        ProgressDialog dialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog1 = ProgressDialog.show(DrClinicActivity.this, "", "در حال بروزرسانی اطلاعات ...");
            dialog1.getWindow().setGravity(Gravity.END);
            dialog1.setCancelable(true);
            insertBtn.setClickable(false);
            office = G.officeInfo.clone();
            City selectedCity = (City) city.getSelectedItem();
            office.setCityId(selectedCity.GetCityID());
            office.setCityName(selectedCity.GetCityName());
            Expert selectedExpert = (Expert) expert.getSelectedItem();
            office.setExpertId(selectedExpert.getId());
            office.setExpertName(selectedExpert.getName());
            SubExpert selectedSubExpert = (SubExpert) subExpert.getSelectedItem();
            office.setSubExpertId(selectedSubExpert.getId());
            office.setSubExpertName(selectedSubExpert.getName());
            office.setAddress(Address.getText().toString().trim());
            office.setPhone(phone.getText().toString().trim());
            office.setBiography(biography.getText().toString().trim());
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeUpdateOfficeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), office);
            } catch (MyException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            insertBtn.setClickable(true);
            if (msg != null) {
                dialog1.dismiss();
                new MessageBox(DrClinicActivity.this, msg).show();
            } else {
                if (result) {
                    G.officeInfo = office;
                    dialog1.dismiss();
                    Toast.makeText(DrClinicActivity.this, "ثبت مشخصات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    dialog1.dismiss();
                    new MessageBox(DrClinicActivity.this, "ثبت مشخصات با مشکل مواجه شد !").show();
                }
            }
        }
    }
}
