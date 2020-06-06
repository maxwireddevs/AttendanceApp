package com.wireddevs.attendanceapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.ViewStudentsRecyclerView.ViewStudentRecyclerViewAdapter;
import com.wireddevs.attendanceapp.database.DatabaseHelper;
import com.wireddevs.attendanceapp.database.model.Student;
import com.wireddevs.attendanceapp.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainTabs extends AppCompatActivity {

    public static MainTabs context;
    private static ArrayList<Student> searchResult=new ArrayList<>();
    private ViewStudentRecyclerViewAdapter adapter;

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
        refreshSearchResult();
        adapter = new ViewStudentRecyclerViewAdapter(this, R.layout.list_item_students_view, searchResult);

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
                aboutbuilder.setMessage("For enquiries contact via email:"+"\n"+"madoka300900@gmail.com");
                aboutbuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                aboutbuilder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<Student> getSearchResult(){
                return searchResult;
    }

    public ViewStudentRecyclerViewAdapter getAdapter(){return adapter;}

    public void refreshSearchResult(){
        searchResult.clear();
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor res = db.getAllData();
        while (res.moveToNext()) {
            Student studentFound=new Student();
            studentFound.setId(res.getInt(0));
            studentFound.setTimestamp(res.getString(1));
            studentFound.setName(res.getString(2));
            searchResult.add(studentFound);
        }
        Collections.sort(searchResult, new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshSearchResult();
        adapter.notifyDataSetChanged();
    }
}
