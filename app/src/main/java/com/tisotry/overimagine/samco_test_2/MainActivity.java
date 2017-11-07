package com.tisotry.overimagine.samco_test_2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tisotry.overimagine.samco_test_2.Util.ConnectDrone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    // ConnectDrone
    private ConnectDrone mConnectDrone = new ConnectDrone(this);

    // Drawer
    DrawerLayout drawer;

    // Maps
    private GoogleMap mMap;

    // Connect Status
    private boolean bool_Connect_Drone = false;

    // TextView
    TextView txt_connect_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer_right = (DrawerLayout) findViewById (R.id.layout_drawer_right)

        NavigationView nav_Left = (NavigationView) findViewById(R.id.nav_left);
        nav_Left.setNavigationItemSelectedListener(this);

        View nav_header_view = nav_Left.getHeaderView(0);


        txt_connect_status = (TextView) nav_header_view.findViewById(R.id.nav_txt_status);


        NavigationView nav_Right = (NavigationView) findViewById(R.id.nav_right);
        nav_Right.setNavigationItemSelectedListener(this);

        // Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        findViewById(R.id.main_btn_openDrawer).setOnClickListener(this);


    }


    @Override
    protected void onResume() {
        super.onResume();

        bool_Connect_Drone = mConnectDrone.getConnectStatus();
        if (bool_Connect_Drone)
            txt_connect_status.setText(R.string.connected);
        else
            txt_connect_status.setText(R.string.disconnected);

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
            case R.id.nav_gallery:
//                startActivity(new Intent(this, MapsActivity.class));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_openDrawer:
                drawer.openDrawer(GravityCompat.START);
        }
    }
}