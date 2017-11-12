package com.tisotry.overimagine.samco_test_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tisotry.overimagine.samco_test_2.Util.ConnectFCC;
import com.tisotry.overimagine.samco_test_2.Util.Status;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    // ConnectFCC
    private ConnectFCC mConnectDrone = new ConnectFCC(this);
    //    private Status mStatus = new Status(this);

    // Drawer
    DrawerLayout drawer;

    // Maps
    private GoogleMap mMap;

    // TextView
    public TextView txt_phone_battery;
    public TextView txt_fcc_battery;


    // Header
    // TextView
    public TextView header_left_battery_phone;
    public TextView header_left_battery_fcc;

    // Menu
    // Left Drawer
    public MenuItem nav_connect;
    public MenuItem nav_disconnect;
    public MenuItem nav_reconnect;

    // Right Drawer
    MenuItem bat_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_phone_battery = findViewById(R.id.main_battery_phone);
        txt_fcc_battery = findViewById(R.id.main_battery_fcc);

        // Drawer init
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Left NavigationView
        NavigationView nav_Left = (NavigationView) findViewById(R.id.nav_left);
        nav_Left.setNavigationItemSelectedListener(this);

        View nav_header_view = nav_Left.getHeaderView(0);
        Menu menu_left = nav_Left.getMenu();


        // header
        header_left_battery_phone = (TextView) nav_header_view.findViewById(R.id.nav_left_battery_phone);
        header_left_battery_fcc = (TextView) nav_header_view.findViewById(R.id.nav_left_battery_fcc);

        // menu
        nav_connect = menu_left.findItem(R.id.nav_connect);
        nav_disconnect = menu_left.findItem(R.id.nav_disconnect);
        nav_reconnect = menu_left.findItem(R.id.nav_reconnect);

        // Right NavigationView
        NavigationView nav_Right = (NavigationView) findViewById(R.id.nav_right);
        nav_Right.setNavigationItemSelectedListener(this);
        Menu menu_right = nav_Right.getMenu();

        bat_device = (MenuItem) menu_right.findItem(R.id.bat_phone);

        // Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    // 뒤로가기 2번 눌러 종료
    private boolean checkExit = false;
    @SuppressLint("HandlerLeak")
    private Handler exitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                checkExit = false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) | drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.START);
            drawer.closeDrawer(GravityCompat.END);
        } else {
//            super.onBackPressed();
            if (!checkExit) {
                Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                checkExit = true;
                exitHandler.sendEmptyMessageDelayed(0, 2000);
            } else finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_connect:
                mConnectDrone.connectDialog();

                break;
            case R.id.nav_disconnect:
                mConnectDrone.disconnectDialog();
                break;
            case R.id.nav_reconnect:
                mConnectDrone.reconnect();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ikw = new LatLng(36.1694533, 128.4678934);
        mMap.addMarker(new MarkerOptions().position(ikw).title("Gyeongwoon Univ"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ikw, 16));
    }
}
