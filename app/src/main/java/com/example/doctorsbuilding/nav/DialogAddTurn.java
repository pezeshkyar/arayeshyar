package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DialogAddTurn extends DialogFragment {

    private ViewFlipper viewFlipper;
    private CheckBox myCheckBox;
    private Context context;
    private Turn turnData;
    private Spinner spinnerTaskGroup;
    private Spinner spinnerTask;
    private Spinner spinnerAssistant;
    private Button taskBackBtn;
    private Button btnAddTurn;
    private TextView patientName;

    private TextView chBoxTitle_Dr;
    private TextView taskPrice;
    private ListView mListView;
    private EditText user_Username;
    private EditText user_firstname;
    private EditText user_lastname;
    private Button user_btnSearch;

    private TextView chboxTitle_User;
    private EditText guest_Name;
    private EditText guest_lastname;
    private EditText guest_mobile;
    private Button guest_btnNext;
    private CustomReservationListAdapter adapterUser;
    private ArrayAdapter<Task> adapterTask;
    private ArrayAdapter<TaskGroup> adapterTaskGroup;
    private ArrayAdapter<Assistant> adapterAssistant;

    private DialogCallback dialogCallback;
    private View rootView;

    private int resevationId = -1;
    private int selecteItemPosition = -1;

    private static final int SEARCH_PAGE = 0;
    private static final int PATIENT_PAGE = 1;
    private static final int GET_TURN_PAGE = 2;


    public DialogFragment setCallBack(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_turn, container, false);
        initViews();
        viewListener();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogCallback.getResult(resevationId);
    }

    public static DialogAddTurn newInstance(Context context, Turn turnData) {
        DialogAddTurn frag = new DialogAddTurn();
        frag.setTurnData(turnData);
        frag.setContext(context);
        return frag;
    }

    public void setTurnData(Turn turn) {
        turnData = turn;
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    private void initViews() {
        taskPrice = (TextView) rootView.findViewById(R.id.addTurn_taskPrice);
        spinnerTask = (Spinner) rootView.findViewById(R.id.addTurn_spinner_task);
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.addTurn_viewSwitcher);
        myCheckBox = (CheckBox) rootView.findViewById(R.id.addTurn_chbox);
        spinnerTaskGroup = (Spinner) rootView.findViewById(R.id.addTurn_spinner_taskGroup);
        taskBackBtn = (Button) rootView.findViewById(R.id.addTask_backBtn);
        btnAddTurn = (Button) rootView.findViewById(R.id.addTurn_btnAddTurn);
        patientName = (TextView) rootView.findViewById(R.id.addTurn_patientName);

        chBoxTitle_Dr = (TextView) rootView.findViewById(R.id.addTurn_chbox_drTitle);
        mListView = (ListView) rootView.findViewById(R.id.addTurn_listView);
        user_Username = (EditText) rootView.findViewById(R.id.addTurn_search_username);
        user_firstname = (EditText) rootView.findViewById(R.id.addTurn_search_name);
        user_lastname = (EditText) rootView.findViewById(R.id.addTurn_search_lastname);
        user_btnSearch = (Button) rootView.findViewById(R.id.addTurn_btnSearch);

        chboxTitle_User = (TextView) rootView.findViewById(R.id.addTurn_chbox_userTitle);
        guest_btnNext = (Button) rootView.findViewById(R.id.addTurn_guest_btnNext);
        guest_Name = (EditText) rootView.findViewById(R.id.addTurn_guest_name);
        guest_lastname = (EditText) rootView.findViewById(R.id.addTurn_guest_lastname);
        guest_mobile = (EditText) rootView.findViewById(R.id.addTurn_guest_mobile);

        adapterUser = new CustomReservationListAdapter(context, new ArrayList<User>());
        mListView.setAdapter(adapterUser);
        adapterTask = new ArrayAdapter<Task>(context, R.layout.spinner_item);
        adapterTaskGroup = new ArrayAdapter<TaskGroup>(context, R.layout.spinner_item);
        spinnerTask.setAdapter(adapterTask);
        spinnerTaskGroup.setAdapter(adapterTaskGroup);
        spinnerAssistant = (Spinner) rootView.findViewById(R.id.addTurn_spinner_assistant);
        adapterAssistant = new ArrayAdapter<Assistant>(context, R.layout.spinner_item);
        spinnerAssistant.setAdapter(adapterAssistant);

        if (G.officeInfo.getRole() == UserType.User.ordinal()) {
            taskBackBtn.setVisibility(View.INVISIBLE);
            chBoxTitle_Dr.setVisibility(View.GONE);
            chboxTitle_User.setVisibility(View.VISIBLE);
            guest_Name.setText(G.UserInfo.getFirstName());
            guest_lastname.setText(G.UserInfo.getLastName());
            guest_mobile.setText(G.UserInfo.getPhone());
            guest_Name.setEnabled(false);
            guest_lastname.setEnabled(false);
            guest_mobile.setEnabled(false);
            viewFlipper.setDisplayedChild(PATIENT_PAGE);

        }

    }

    private void viewListener() {

        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                UserType usr = UserType.values()[G.officeInfo.getRole()];
                switch (usr) {
                    case Dr:
                    case secretary:
                        if (checked) {
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(SEARCH_PAGE);
                        } else {
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                        }
                        break;
                    case User:
                        if (checked) {
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                            guest_Name.setText(G.UserInfo.getFirstName());
                            guest_lastname.setText(G.UserInfo.getLastName());
                            guest_mobile.setText(G.UserInfo.getPhone());
                            guest_Name.setEnabled(false);
                            guest_lastname.setEnabled(false);
                            guest_mobile.setEnabled(false);
                        } else {
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                            guest_Name.setEnabled(true);
                            guest_lastname.setEnabled(true);
                            guest_mobile.setEnabled(true);
                            guest_Name.setText("");
                            guest_lastname.setText("");
                            guest_mobile.setText("");
                        }
                        break;
                }

            }
        });

        adapterUser.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, LviActionType actionType) {
                if (actionType == LviActionType.select) {
                    selecteItemPosition = position;
                    patientName.setText(((User) adapterUser.getItem(position)).getFullName());
                    viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                    if (adapterTaskGroup.isEmpty())
                        getTaskGroups();
                }

            }
        });
        user_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUser();
            }
        });

        taskBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserType usr = UserType.values()[G.officeInfo.getRole()];
                if (myCheckBox.isChecked()) {
                    switch (usr) {
                        case Dr:
                        case secretary:
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(SEARCH_PAGE);
                            break;
                        default:
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                            break;
                    }
                } else {
                    switch (usr) {
                        case Dr:
                        case secretary:
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                            break;
                        default:
                            taskBackBtn.setVisibility(View.INVISIBLE);
                            viewFlipper.setDisplayedChild(PATIENT_PAGE);
                            break;
                    }
                }

            }
        });

        btnAddTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapterAssistant.isEmpty())
                    return;

                UserType usr = UserType.values()[G.officeInfo.getRole()];
                if (myCheckBox.isChecked()) {
                    switch (usr) {
                        case Dr:
                        case secretary:
                            reserveTurnForUser(((User) adapterUser.getItem(selecteItemPosition)).getUserName());
                            break;
                        default:
                            if (Integer.valueOf(Util.getNumber(taskPrice.getText().toString())) != 0) {
                                G.reservationInfo = getPayInfo();
                                G.reservationInfo.setOwner(UserType.User);
                                Intent intent = new Intent(context, ActivityFactor.class);
                                intent.putExtra("requestCode", UserType.User.ordinal());
                                startActivityForResult(intent, UserType.User.ordinal());
                            } else {
                                reserveTurnForMe();
                            }
                            break;
                    }

                } else {
                    switch (usr) {
                        case Dr:
                        case secretary:
                            reserveTurnForGuest(guest_Name.getText().toString().trim(),
                                    guest_lastname.getText().toString().trim(),
                                    guest_mobile.getText().toString().trim());
                            break;
                        default:
                            if (Integer.valueOf(Util.getNumber(taskPrice.getText().toString())) != 0) {
                                G.reservationInfo = getPayInfo();
                                G.reservationInfo.setOwner(UserType.Guest);
                                Intent intent = new Intent(context, ActivityFactor.class);
                                intent.putExtra("requestCode", UserType.Guest.ordinal());
                                startActivityForResult(intent, UserType.Guest.ordinal());
                            } else {
                                reserveTurnForGuestFromUser(guest_Name.getText().toString().trim(),
                                        guest_lastname.getText().toString().trim(),
                                        guest_mobile.getText().toString().trim());
                            }
                            break;
                    }
                }
            }
        });


        guest_btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    UserType usr = UserType.values()[G.officeInfo.getRole()];
                    switch (usr) {
                        case Dr:
                        case secretary:
                            patientName.setText(guest_Name.getText().toString().trim().concat(" " + guest_lastname.getText().toString().trim()));
//                            viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                            if (adapterTaskGroup.isEmpty()) {
                                getTaskGroups();
                            } else {
                                viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                                taskBackBtn.setVisibility(View.VISIBLE);
                            }
                            break;
                        default:
                            if (myCheckBox.isChecked()) {

                                patientName.setText(guest_Name.getText().toString().trim().concat(" " + guest_lastname.getText().toString().trim()));
//                                viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                                if (adapterTaskGroup.isEmpty()) {
                                    getTaskGroups();
                                } else {
                                    viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                                    taskBackBtn.setVisibility(View.VISIBLE);
                                }

                            } else {

                                patientName.setText(guest_Name.getText().toString().trim().concat(" " + guest_lastname.getText().toString().trim()));
//                                viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                                if (adapterTaskGroup.isEmpty()) {
                                    getTaskGroups();
                                } else {
                                    viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                                    taskBackBtn.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                    }

                }
            }
        });

        spinnerTaskGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                getTask();
                getAssistantForTurn();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                taskPrice.setText(Util.getCurrency(((Task) spinnerTask.getSelectedItem()).getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserType.User.ordinal()) {
            if (G.officeInfo.getRole() == UserType.User.ordinal()) {
                reserveTurnForMe();
            }
        }
        if (resultCode == UserType.Guest.ordinal()) {
            if (G.officeInfo.getRole() == UserType.User.ordinal()) {

                reserveTurnForGuestFromUser(guest_Name.getText().toString().trim(),
                        guest_lastname.getText().toString().trim(),
                        guest_mobile.getText().toString().trim());
            }
        }
    }

    private Reservation getPayInfo() {
        Reservation reserve = getReservation();
        ExpChild child = new ExpChild(turnData);
        reserve.setDate(child.getDate());
        reserve.setTime(child.getTime());
        reserve.setPrice(Integer.valueOf(Util.getNumber(taskPrice.getText().toString())));
        return reserve;
    }

    private boolean checkField() {
        if (guest_Name.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا نام مشتری را وارد نمایید .").show();
            return false;
        }
        if (guest_lastname.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا نام خانوادگی مشتری را وارد نمایید .").show();
            return false;
        }
        if (guest_mobile.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا شماره تلفن همراه مشتری را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    private void searchUser() {
        final ProgressDialog dialog = ProgressDialog.show(context, "", Util.getStringWS(R.id.dialog_waiting));
        dialog.getWindow().setGravity(Gravity.END);
        MyObservable.searchUser(
                user_Username.getText().toString().trim(),
                user_firstname.getText().toString().trim(),
                user_lastname.getText().toString().trim())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<User> users) {
                        dialog.dismiss();
                        if (!users.isEmpty())
                            adapterUser.addAll(users);
                        else
                            Toast.makeText(context, "مشتری با این مشخصات یافت نشد .", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }

    private void reserveTurnForUser(String patientUserName) {
        final ProgressDialog dialog = ProgressDialog.show(context, "", Util.getStringWS(R.id.dialog_waiting));
        dialog.getWindow().setGravity(Gravity.END);

        Reservation reservation = getReservation();
        reservation.setPatientUserName(patientUserName);

        MyObservable.reserveTurnForUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        dialog.dismiss();
                        if (result > 0) {
                            resevationId = result;
                            Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });
    }


    private void reserveTurnForGuest(String PatientFirstName, String PatientLastName, String PatientPhoneNo) {
        final ProgressDialog dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
        dialog.getWindow().setGravity(Gravity.END);

        Reservation reservation = getReservation();
        reservation.setPatientFirstName(PatientFirstName);
        reservation.setPatientLastName(PatientLastName);
        reservation.setPatientPhoneNo(PatientPhoneNo);
        reservation.setCityId(G.UserInfo.getCityID());

        MyObservable.reserveTurnForGuest(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.UserInfo.getCityID())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        dialog.dismiss();
                        if (result > 0) {
                            resevationId = result;
                            Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });

    }

    private void reserveTurnForGuestFromUser(String patientFirstName, String patientLastName, String patientPhoneNo) {
        final ProgressDialog dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
        dialog.getWindow().setGravity(Gravity.END);

        Reservation reservation = getReservation();
        reservation.setPatientFirstName(patientFirstName);
        reservation.setPatientLastName(patientLastName);
        reservation.setPatientPhoneNo(patientPhoneNo);
        reservation.setCityId(G.UserInfo.getCityID());

        MyObservable.reserveTurnForGuestFromUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        dialog.dismiss();
                        switch (result) {
                            case 0:
                                new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                                break;
                            case -1:
                                new MessageBox(context, Util.getStringWS(R.string.addturn_etebar_error)).show();
                                break;
                            default:
                                if(result > 0) {
                                    resevationId = result;
                                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(context, e.getMessage()).show();
                        G.resNum = -1;
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                        G.resNum = -1;
                    }
                });


    }

    private void reserveTurnForMe() {
        final ProgressDialog dialog = ProgressDialog.show(context, "", Util.getStringWS(R.id.dialog_waiting));
        dialog.getWindow().setGravity(Gravity.END);

        Reservation reservation = getReservation();

        MyObservable.reserveTurnForMe(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        dialog.dismiss();
                        switch (result) {
                            case 0:
                                new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                                break;
                            case -1:
                                new MessageBox(context, Util.getStringWS(R.string.addturn_etebar_error)).show();
                                break;
                            default :
                                if(result > 0) {
                                    resevationId = result;
                                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        new MessageBox(context, e.getMessage()).show();
                        G.resNum = -1;
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                        G.resNum = -1;
                    }
                });
    }

    private void getTaskGroups() {

        MyObservable.getTaskGroups(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), turnData.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TaskGroup>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<TaskGroup> taskGroups) {
                        if (taskGroups.isEmpty()) {
                            new MessageBox(context, "خدماتی برای آرایشگاه ثبت نشده است .").show();
                        } else {
                            taskBackBtn.setVisibility(View.VISIBLE);
                            viewFlipper.setDisplayedChild(GET_TURN_PAGE);
                            adapterTaskGroup.addAll(taskGroups);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    private void getTask() {
        int taskGroupId = ((TaskGroup) spinnerTaskGroup.getSelectedItem()).getId();
        adapterTask.clear();
        MyObservable.getTasks(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), taskGroupId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Task> tasks) {
                        if (tasks.isEmpty())
                            new MessageBox(context, "زیر گروه خدمات ثبت نشده است .").show();
                        else
                            adapterTask.addAll(tasks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Reservation getReservation() {

        Reservation reserve = new Reservation();
        reserve.setTurnId(turnData.getId());
        reserve.setFirstReservationId(0);
        reserve.setTaskId(((Task) spinnerTask.getSelectedItem()).getId());
        reserve.setNumberOfTurns(1);
        reserve.setAssistantUsername(((Assistant) spinnerAssistant.getSelectedItem()).getUsername());
        return reserve;
    }


    private void getAssistantForTurn() {
        int taskGroupId = ((TaskGroup) spinnerTaskGroup.getSelectedItem()).getId();
        adapterAssistant.clear();
        MyObservable.getAssistantForTurn(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), turnData.getId(), taskGroupId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Assistant>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Assistant> assistants) {
                        if (assistants.isEmpty()) {
                            new MessageBox(context, "هیچ آرایشگری جهت ارئه خدمات وجود ندارد .").show();
                        } else {
                            adapterAssistant.addAll(assistants);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(context, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

}