package com.example.studentlistapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.studentlistapp.R;
import com.example.studentlistapp.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainTabs extends AppCompatActivity {
    public static MainTabs context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);
        context=this;
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setCurrentItem(2);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_about:
                AlertDialog.Builder aboutbuilder=new AlertDialog.Builder(this);
                aboutbuilder.setCancelable(true);
                aboutbuilder.setTitle("About");
                aboutbuilder.setMessage("For enquiries contact via WhatsApp: (+65)84276746");
                aboutbuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                aboutbuilder.show();
                return true;
            case R.id.menu_settings:
                Toast.makeText(this, "TODO: Settings", Toast.LENGTH_SHORT).show();
                //TODO Settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
