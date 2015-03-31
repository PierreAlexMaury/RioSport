package com.google.riosport;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;


import java.util.List;
import java.util.Vector;


public class MyFragment extends FragmentActivity implements ActionBar.TabListener{

    private PagerAdapter mPagerAdapter;
    ViewPager pager;
    public static String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_my_fragment);

        // Création de la liste de Fragments que fera défiler le PagerAdapter
        List fragments = new Vector();

        // Ajout des Fragments dans la liste
        fragments.add(Fragment.instantiate(this,LeftFragmentPage.class.getName()));
        fragments.add(Fragment.instantiate(this,MiddleFragmentPage.class.getName()));
        fragments.add(Fragment.instantiate(this,RightFragmentPage.class.getName()));

        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);


        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        pager = (ViewPager) super.findViewById(R.id.viewpager);
        // Affectation de l'adapter au ViewPager
        pager.setAdapter(this.mPagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
//                Toast.makeText(getApplicationContext(), "onPageSelected, pos=" + position,
//                        Toast.LENGTH_SHORT).show();

            }
    });

        actionBar.addTab(
                actionBar.newTab()
                        .setText(R.string.friend)
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(R.string.feed)
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(R.string.map)
                        .setTabListener(this));

        if(Main.init_fragment==true) {
          pager.setCurrentItem(2);
        }


        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);


    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment, menu);
        return true;
    }


    //disable the back touch by redefine its action: do nothing
    @Override
    public void onBackPressed() {
        AlertDialog.Builder box = new AlertDialog.Builder(this);
        box.setIcon(R.drawable.rs);
        box.setTitle("Quit RioSport ?");
        box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("LOGOUT", true);
                        startActivity(intent);
                    }
                }
        );

        box.setNegativeButton("No", null);
        box.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.deconnect:
                Toast.makeText(getBaseContext(), "Sign out ...", Toast.LENGTH_SHORT).show();
                    Main.sign_out=true;
                    Intent intent = new Intent(MyFragment.this, Main.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);

                    startActivity(intent);
                break;
            case R.id.quit:
                AlertDialog.Builder box = new AlertDialog.Builder(this);
                box.setIcon(R.drawable.rs);
                box.setTitle("Quit RioSport ?");
                box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), Main.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("LOGOUT", true);
                                startActivity(intent);
                            }
                        }
                );

                box.setNegativeButton("No", null);
                box.show();
                break;
            case R.id.create_event:
                Intent create_event = new Intent(MyFragment.this, CreateEvent.class);
                startActivityForResult(create_event, 10);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}
