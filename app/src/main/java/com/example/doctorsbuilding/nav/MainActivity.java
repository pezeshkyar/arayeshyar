package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.DrClinicActivity;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatActivity;
import com.example.doctorsbuilding.nav.Dr.Notification.ManagementNotificationActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.DrProfileActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoActivity;
import com.example.doctorsbuilding.nav.Question.ActivityCreateQuestion;
import com.example.doctorsbuilding.nav.User.UserInboxActivity;
import com.example.doctorsbuilding.nav.User.UserMyNobatActivity;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.support.ActivityTickets;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readystatesoftware.viewbadger.BadgeView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    NavigationView navigationView = null;
    GoogleMap mMap;
    BadgeView badge;
    RelativeLayout unreadMessageLayout;
    TextView drName;
    TextView drExpert;
    TextView drAddress;
    TextView drPhone;
    TextView drBiography;
    TextView menu_header_name;
    TextView menu_header_version;
    Button RightFloatButton;
    Button LeftFloatButton;
    ImageButton btn_menu;
    SupportMapFragment mapFragment;
    DatabaseAdapter database;
    CirclePageIndicator indicator;
    DrawerLayout mDrawerLayout;
    ImageView menu_header_image;
    ProgressBar baner_progress;
    DrawerLayout drawer;
    ArrayList<PhotoDesc> baners;
    ArrayList<Boolean> banerTaskList;
    ArrayList<MenuItem> menuItems;
    ArrayList<MessageInfo> unreadMessages = null;
    CustomNavListView adapter_nav;
    ListView nav_listview;
    UserType menu = UserType.None;
    static ViewPager mPager;
    final static int IMAGE_PROFILE_ID_USER = 2;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Util.setStatusBarColor(MainActivity.this);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
        getGalleryPicIds();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePage();
    }

    private void initViews() {
        RightFloatButton = (Button) findViewById(R.id.main_activity_floatbtn1);
        LeftFloatButton = (Button) findViewById(R.id.main_activity_floatbtn2);
        UserType usr = UserType.values()[G.officeInfo.getRole()];
        switch (usr) {
            case User:
                RightFloatButton.setText("دریافت نوبت");
                LeftFloatButton.setText("تماس تلفنی");
                break;
            case Assistant:
                RightFloatButton.setText("لیست پذیرش");
                LeftFloatButton.setText("تماس تلفنی");
                break;
            case Dr:
            case secretary:
                RightFloatButton.setText("لیست پذیرش");
                LeftFloatButton.setText("اطلاع رسانی");
                break;
        }

        menuItems = new ArrayList<MenuItem>();
        unreadMessageLayout = (RelativeLayout) findViewById(R.id.unreadMessage1);
        badge = new BadgeView(MainActivity.this, unreadMessageLayout);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.badgeColor));
        database = new DatabaseAdapter(MainActivity.this);
        adapter_nav = new CustomNavListView(MainActivity.this, new ArrayList<MenuItem>());
        nav_listview = (ListView) findViewById(R.id.mainActivity_nav_lv);
        nav_listview.setAdapter(adapter_nav);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewCompat.setLayoutDirection(drawer, ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        final Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar33);
        mainToolbar.setContentInsetsAbsolute(0, 0);
        mPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        baner_progress = (ProgressBar) findViewById(R.id.baner_progress);
        drName = (TextView) findViewById(R.id.content_main_name);
        drExpert = (TextView) findViewById(R.id.content_main_expert);
        drAddress = (TextView) findViewById(R.id.content_main_address);
        drPhone = (TextView) findViewById(R.id.content_main_tel);
        drBiography = (TextView) findViewById(R.id.content_main_biography);
        btn_menu = (ImageButton) findViewById(R.id.menu_btn);

        setNavigationDrawer();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void eventListener() {
        UserType usr = UserType.values()[G.officeInfo.getRole()];
        switch (usr) {
            case User:
                RightFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                    }
                });
                LeftFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + G.officeInfo.getPhone()));
                        startActivity(intent);
                    }
                });
                break;
            case Assistant:
                RightFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, ActivityPatientAssistant.class));
                    }
                });
                LeftFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + G.officeInfo.getPhone()));
                        startActivity(intent);
                    }
                });
                break;
            case Dr:
            case secretary:
                RightFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, ActivityPatientListToday.class));
                    }
                });
                LeftFloatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, ManagementNotificationActivity.class));
                    }
                });

                break;
        }

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        nav_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (menuItems.get(position).getItemId()) {
                    case R.id.nav2_clinic:
                        startActivity(new Intent(MainActivity.this, DrClinicActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_addTurn:
                        startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_manage_nobat:
                        startActivity(new Intent(MainActivity.this, DrNobatActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_gallery:
                        startActivity(new Intent(MainActivity.this, gallery2.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_taskes:
                        startActivity(new Intent(MainActivity.this, ActivityManagementTaskes.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_secretary:
                        startActivity(new Intent(MainActivity.this, ActivityManageSecretary.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_map:
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_patientFile:
                        startActivity(new Intent(MainActivity.this, ActivitySearchPatient.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_question:
                        startActivity(new Intent(MainActivity.this, ActivityCreateQuestion.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_user_patientFile:
                        Intent intent = new Intent(MainActivity.this, ActivityPatientFile.class);
                        intent.putExtra("patientUserName", G.UserInfo.getUserName());
                        startActivity(intent);
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_my_nobat:
                        startActivity(new Intent(MainActivity.this, UserMyNobatActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_news:
                        startActivity(new Intent(MainActivity.this, UserNewsActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_assistant:
                        startActivity(new Intent(MainActivity.this, ActivityAssistant.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_myOffice:
                        startActivity(new Intent(MainActivity.this, ActivityMyOffices.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_support:
                        startActivity(new Intent(MainActivity.this, ActivityTickets.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_logout:
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_account:
                        startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_inbox:
                        startActivity(new Intent(MainActivity.this, UserInboxActivity.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_etebar:
                        startActivity(new Intent(MainActivity.this, ActivityEtebar.class));
                        drawer.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.nav2_setting:
                        setNavigationSetttingItem(menu);
                        break;
                    case R.id.nav2_back:
                        setNavigationItem(menu);
                        break;

                    default:
                        break;
                }
            }
        });

        unreadMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unreadMessages != null && unreadMessages.size() != 0) {
                    ActivityNotificationDialog dialog = new ActivityNotificationDialog(MainActivity.this, unreadMessages);
                    dialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "هیچ پیام جدیدی برای خواندن وجود ندارد .", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updatePage() {

        getUnreadMessage();
        database = new DatabaseAdapter(MainActivity.this);
        loadUser();
        if (G.officeInfo != null) {
            drName.setText(G.officeInfo.getFirstname().concat(" " + G.officeInfo.getLastname()));
            drExpert.setText(G.officeInfo.getSubExpertName());
            drAddress.setText(G.officeInfo.getAddress());
            drPhone.setText(G.officeInfo.getPhone());
            drBiography.setText(G.officeInfo.getBiography());
            mapFragment.getMapAsync(this);
        }
        if (G.UserInfo.getImgProfile() == null) {
            int id = R.drawable.doctor;
            try {
                if (database.openConnection()) {
                    G.UserInfo.setImgProfile(database.getImageProfile(IMAGE_PROFILE_ID_USER));
                }
            } catch (MyException e) {
                new MessageBox(MainActivity.this, e.getMessage()).show();
            }
            G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getBaseContext().getResources(), id));
        }

//        if (menuItems.size() == 0)
        setNavigationViewMenu(menu);
    }

    private void initSlideShow(ArrayList<Integer> imageIdsInWeb) {

        banerTaskList = new ArrayList<Boolean>();
        baners = new ArrayList<PhotoDesc>();
        for (int i = 0; i < imageIdsInWeb.size(); i++) {
            banerTaskList.add(false);
            PhotoDesc aks = new PhotoDesc();
            aks.setId(imageIdsInWeb.get(i));
            aks.setDescription("");
            aks.setDate("");
            aks.setPhoto(BitmapFactory.decodeResource(getResources(), R.mipmap.image_placeholder));
            baners.add(aks);
        }
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, baners));
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        banerTaskList.set(0, true);
        getGalleryPicFromPhone(baners.get(0).getId(), 0);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (!banerTaskList.get(position)) {
                    banerTaskList.set(position, true);
                    getGalleryPicFromPhone(baners.get(position).getId(), position);
                }
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(G.officeInfo.getLatitude(), G.officeInfo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void loadUser() {
        menu = UserType.values()[G.officeInfo.getRole()];
        switch (menu) {
            case Dr:
                menu = UserType.Dr;
                break;
            case User:
                menu = UserType.User;
                break;
            case secretary:
                menu = UserType.secretary;
                break;
            case Assistant:
                menu = UserType.Assistant;
                break;
            case None:
                menu = UserType.Guest;
            default:
                break;
        }

    }

    private void setNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu_header_image = (ImageView) navigationView.findViewById(R.id.img_profile33);
        menu_header_name = (TextView) navigationView.findViewById(R.id.name33);
        menu_header_version = (TextView) navigationView.findViewById(R.id.pezashyar_type33);
    }

    private void setNavigationSetttingItem(UserType user) {
        PopupMenu p = new PopupMenu(this, null);
        Menu menu = p.getMenu();
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        adapter_nav.removeAll();
        menuItems.clear();
        switch (user) {
            case Dr:
                menuItems.add(menu.findItem(R.id.nav2_back));
                menuItems.add(menu.findItem(R.id.nav2_account));
                menuItems.add(menu.findItem(R.id.nav2_secretary));
                menuItems.add(menu.findItem(R.id.nav2_assistant));
                menuItems.add(menu.findItem(R.id.nav2_taskes));
                menuItems.add(menu.findItem(R.id.nav2_clinic));
                menuItems.add(menu.findItem(R.id.nav2_map));
                menuItems.add(menu.findItem(R.id.nav2_question));
                break;
            case secretary:
                menuItems.add(menu.findItem(R.id.nav2_back));
                menuItems.add(menu.findItem(R.id.nav2_account));
                menuItems.add(menu.findItem(R.id.nav2_assistant));
                menuItems.add(menu.findItem(R.id.nav2_taskes));
                menuItems.add(menu.findItem(R.id.nav2_clinic));
                menuItems.add(menu.findItem(R.id.nav2_map));
                menuItems.add(menu.findItem(R.id.nav2_question));
                break;
        }
        adapter_nav.addAll(menuItems);
    }

    private void setNavigationItem(UserType user) {
        PopupMenu p = new PopupMenu(this, null);
        Menu menu = p.getMenu();
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        adapter_nav.removeAll();
        menuItems.clear();
        switch (user) {
            case User:
                menuItems.add(menu.findItem(R.id.nav2_myOffice));
                menuItems.add(menu.findItem(R.id.nav2_account));
                menuItems.add(menu.findItem(R.id.nav2_etebar));
                menuItems.add(menu.findItem(R.id.nav2_my_nobat));
                menuItems.add(menu.findItem(R.id.nav2_user_patientFile));
                menuItems.add(menu.findItem(R.id.nav2_gallery));
                menuItems.add(menu.findItem(R.id.nav2_inbox));
                menuItems.add(menu.findItem(R.id.nav2_support));
                break;
            case Dr:
            case secretary:
                menuItems.add(menu.findItem(R.id.nav2_myOffice));
                menuItems.add(menu.findItem(R.id.nav2_manage_nobat));
                menuItems.add(menu.findItem(R.id.nav2_addTurn));
                menuItems.add(menu.findItem(R.id.nav2_patientFile));
                menuItems.add(menu.findItem(R.id.nav2_gallery));
                menuItems.add(menu.findItem(R.id.nav2_inbox));
                menuItems.add(menu.findItem(R.id.nav2_setting));
                menuItems.add(menu.findItem(R.id.nav2_support));
                break;
            case Assistant:
                menuItems.add(menu.findItem(R.id.nav2_myOffice));
                menuItems.add(menu.findItem(R.id.nav2_account));
                menuItems.add(menu.findItem(R.id.nav2_inbox));
                menuItems.add(menu.findItem(R.id.nav2_support));
                break;
        }
        adapter_nav.addAll(menuItems);
    }


    private void setNavigationViewMenu(UserType menu) {

        setNavigationItem(menu);
        menu_header_name.setText(G.UserInfo.getFirstName().concat(" " + G.UserInfo.getLastName()));
        try {
            menu_header_image.setImageBitmap(G.UserInfo.getImgProfile());
        } catch (Exception ex) {
            menu_header_image.setImageResource(R.drawable.doctor);
        }
        switch (menu) {
            case Dr:
                menu_header_version.setText("نسخه آرایشگر");
                break;
            case secretary:
                menu_header_version.setText("نسخه منشی");
                break;
            case User:
                menu_header_version.setText("نسخه مشتری");
                break;
            case Assistant:
                menu_header_version.setText("نسخه همکار آرایشی");
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دو بار دکمه BACK را بزنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void getGalleryPicIds() {

        MyObservable.getGalleryPicIds(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Integer> picIds) {
                        if (picIds.isEmpty()) {
                            banerTaskList = new ArrayList<Boolean>();
                            baners = new ArrayList<PhotoDesc>();
                            PhotoDesc aks = new PhotoDesc();
                            aks.setId(-1);
                            aks.setDescription("");
                            aks.setDate("");
                            aks.setPhoto(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor_temp));
                            baners.add(aks);

                            mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, baners));
                            indicator.setVisibility(View.GONE);

                        } else {
                            indicator.setVisibility(View.VISIBLE);
                            initSlideShow(picIds);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(MainActivity.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {
                        baner_progress.setVisibility(View.GONE);
                    }
                });
    }

    private void getGalleryPicFromPhone(final int photoId, final int currentPageNum) {
        MyObservable.getGalleryPicFromPhone(photoId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PhotoDesc>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PhotoDesc photo) {
                        baners.set(currentPageNum, photo);
                        mPager.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getGalleryPicFromWeb(photoId, currentPageNum);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getGalleryPicFromWeb(int photoId, final int currentPageNum) {


        MyObservable.getGalleryPicFromWeb(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeInfo.getId(), photoId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PhotoDesc>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PhotoDesc photo) {
                        if (photo.photo != null) {
                            baners.set(currentPageNum, photo);
                            mPager.getAdapter().notifyDataSetChanged();
                            try {
                                if (database.openConnection()) {
                                    database.saveImageToGallery(photo.getId(), photo.getDate(),
                                            photo.getDescription(), DbBitmapUtility.getBytes(photo.getPhoto()));
                                }
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(MainActivity.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getUnreadMessage() {
        MyObservable.getAllUnreadMessage(G.UserInfo.getUserName(), G.UserInfo.getPassword())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MessageInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<MessageInfo> messageInfos) {
                        if (!messageInfos.isEmpty()) {
                            unreadMessages = new ArrayList<MessageInfo>();
                            unreadMessages.addAll(messageInfos);
                            badge.setText(String.valueOf(messageInfos.size()));
                            badge.show();
                        } else {
                            if (unreadMessages != null)
                                unreadMessages.clear();
                            badge.hide();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        new MessageBox(MainActivity.this, e.getMessage()).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}

