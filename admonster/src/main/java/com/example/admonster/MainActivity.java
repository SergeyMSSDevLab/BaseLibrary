package com.example.admonster;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admonster.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mssdevlab.baselib.BaseActivity;
import com.mssdevlab.baselib.ads.InterstitialManager;
import com.mssdevlab.baselib.ApplicationMode.AppMode;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        binding.setViewModelMain(viewModel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.addCommonMenuItems(navigationView.getMenu(), R.id.nav_common, null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Demonstrate interestial
            InterstitialManager.showInterstitialAd(this,true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_eval) {
            if (InterstitialManager.isAppModeAtLeast(this, AppMode.MODE_EVALUATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restricted feature");
                builder.setMessage("Application mode allows to run an Evaluation feature.")
                        .setCancelable(true).show();
            }
        } else if (id == R.id.nav_no_ads) {
            if (InterstitialManager.isAppModeAtLeast(this, AppMode.MODE_NO_ADS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restricted feature");
                builder.setMessage("Application mode allows to run a NoAds feature.")
                        .setCancelable(true).show();
            }
        } else if (id == R.id.nav_pro) {
            if (InterstitialManager.isAppModeAtLeast(this, AppMode.MODE_PRO)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restricted feature");
                builder.setMessage("Application mode allows to run a Pro feature.")
                        .setCancelable(true).show();
            }
        } else if (id == R.id.nav_demo) {
            if (InterstitialManager.isAppModeAtLeast(this, AppMode.MODE_DEMO)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Restricted feature");
                builder.setMessage("Application mode allows to run a Demo feature.")
                        .setCancelable(true).show();
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else {
            this.onMenuItemSelected(id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
