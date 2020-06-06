package com.wireddevs.attendanceapp.main;

import android.content.Context;


import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.view.AddStudentActivity;
import com.wireddevs.attendanceapp.view.AttendanceHandler;
import com.wireddevs.attendanceapp.view.AttendanceHandlerGraphView;
import com.wireddevs.attendanceapp.view.MainActivity;
import com.wireddevs.attendanceapp.view.ViewStudents;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3,R.string.tab_text_4,R.string.tab_text_5};
    private final Context mContext;
    ViewPager viewPager;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new AddStudentActivity();
            case 1:
                return new ViewStudents();
            case 2:
                return new MainActivity();
            case 3:
                return new AttendanceHandler();
            case 4:
                return new AttendanceHandlerGraphView();
            // Other fragments
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 5;
    }
}