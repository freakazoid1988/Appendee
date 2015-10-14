package com.cfg.appendee;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity implements SelectEventFragment.OnSelectEventFragmentInteractionListener, CreateEventFragment.OnFragmentInteractionListener, ScanningFragment.OnScanningFragmentInteractionListener {


    private ScanningFragment scanningFragment;
    private String[] menuOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            WelcomeFragment welcomeFragment = new WelcomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, welcomeFragment, "WelcomeFragment").commit();
        }

        menuOptions = getResources().getStringArray(R.array.menuOptions);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void addDrawerItems() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuOptions);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Fragment fragment = new SelectEventFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, fragment, "SelectEventFragment").commit();
                        mDrawerList.setItemChecked(position, true);
                        getSupportActionBar().setTitle(menuOptions[position]);
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 1:
                        Fragment fragment1 = new CreateEventFragment();
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        fragmentManager1.beginTransaction().replace(R.id.container, fragment1, "CreateEventFragment").commit();
                        mDrawerList.setItemChecked(position, true);
                        getSupportActionBar().setTitle(menuOptions[position]);
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.select_option);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public void onSelectEventInteraction(int id) {
        ScanningFragment scanningFragment = ScanningFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, scanningFragment, "ScanningFragment").commit();
        getSupportActionBar().setTitle("Seleziona evento");
    }

    @Override
    public void onCreateEventInteraction(Uri uri) {

    }

    @Override
    public void onExportEventInteraction(String tablename) {
        ExportFragment exportFragment = ExportFragment.newInstance(tablename);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, exportFragment, "ExportFragment").commit();
        getSupportActionBar().setTitle("Esporta");
    }

    public void setActionBarTitle(String text) {
        getSupportActionBar().setTitle(text);
    }
}
