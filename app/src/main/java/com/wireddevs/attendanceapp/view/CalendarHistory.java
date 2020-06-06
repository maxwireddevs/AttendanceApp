package com.wireddevs.attendanceapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.AttendanceHelper;
import com.wireddevs.attendanceapp.database.DatabaseHelper;
import com.wireddevs.attendanceapp.database.model.AttendanceStorage;
import com.wireddevs.attendanceapp.database.model.Student;
import com.wireddevs.attendanceapp.utils.TimestampAdjuster;
import com.yanzhenjie.wheel.OnWheelChangedListener;
import com.yanzhenjie.wheel.WheelView;
import com.yanzhenjie.wheel.adapters.ArrayWheelAdapter;
import com.yanzhenjie.wheel.adapters.WheelViewAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class CalendarHistory extends AppCompatActivity {
    ArrayList<AttendanceStorage> dateCalendarList=new ArrayList<>();
    private MCalendarView calendarView;
    final TimestampAdjuster adjusttimestamp=new TimestampAdjuster();
    private int currentmonth;
    private String selectedtimestamp;
    private String selectedattendancehour,selectedattendanceminute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_history);

        Intent intent=getIntent();
        long passableID = intent.getLongExtra("passableID", 0);

        calendarView = ((MCalendarView) findViewById(R.id.calendar));

        DatabaseHelper dh=new DatabaseHelper(this);
        final Student selectedstudent=dh.getStudent(passableID);

        AttendanceHelper ah = new AttendanceHelper(this);
        Cursor res=ah.getAllData();

        dateCalendarList.clear();

        ArrayList<DateData> markedDate=new ArrayList<>(calendarView.getMarkedDates().getAll());

        for(int i=0;i<markedDate.size();i++){
            calendarView.unMarkDate(markedDate.get(i));
        }

        while(res.moveToNext()) {
            if (selectedstudent.getName().equals(res.getString(2))) {
                {
                    dateCalendarList.add(new AttendanceStorage(res.getInt(0),res.getString(1),res.getString(2)));
                }
            }
        }

        TimestampAdjuster ta=new TimestampAdjuster();

        for(int i=0;i<dateCalendarList.size();i++){
            calendarView.markDate(Integer.parseInt(ta.getYear(dateCalendarList.get(i).getTimestamp())), Integer.parseInt(ta.getMonth(dateCalendarList.get(i).getTimestamp())), Integer.parseInt(ta.getDay(dateCalendarList.get(i).getTimestamp())));
        }

        currentmonth=Calendar.getInstance().get(Calendar.MONTH)+1;

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                toggleProfileAttendanceButton(selectedstudent.getName(),date.getYear(),date.getMonth(),date.getDay());
            }
        });

    }

    public void toggleProfileAttendanceButton(final String name, final int year, final int month, final int day){
        Boolean hasContent=false;

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(name+" on "+day+"/"+month+"/"+year);

        final StringBuffer buffer = new StringBuffer();

        for(int i=0;i<dateCalendarList.size();i++){
            final AttendanceStorage selectedattendance=dateCalendarList.get(i);
            final long selectedid=selectedattendance.getId();
            final String selectedtimestamp=selectedattendance.getTimestamp();
            String selectedname=selectedattendance.getName();

            if(year==Integer.parseInt(adjusttimestamp.getYear(selectedtimestamp))){
                if(month==Integer.parseInt(adjusttimestamp.getMonth(selectedtimestamp))) {
                    if (day==Integer.parseInt(adjusttimestamp.getDay(selectedtimestamp))) {
                        if(name.equalsIgnoreCase(selectedname)) {
                            buffer.append("Date: " + adjusttimestamp.getDay(selectedtimestamp) + " " + adjusttimestamp.getStringMonth(selectedtimestamp) + " " + adjusttimestamp.getYear(selectedtimestamp) + "\n");
                            buffer.append("Time: " + adjusttimestamp.getHour(selectedtimestamp) + ":" + adjusttimestamp.getMinute(selectedtimestamp) + "\n");
                            buffer.append("Name: " + selectedname + "\n");
                            hasContent=true;

                            builder.setMessage(buffer);
                            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeAttendance(selectedattendance,year,month,day);
                                }
                            });
                            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editAttendance(selectedattendance);
                                }
                            });

                        }
                    }
                }
            }
        }

        if(hasContent==false){
                builder.setMessage("No attendance");
        }

        builder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });

        builder.show();
    }
    public void removeAttendance(AttendanceStorage attendance,int year,int month,int day){
        final long selectedid=attendance.getId();

        AttendanceHelper ah=new AttendanceHelper(CalendarHistory.this);
        ah.deleteThisAttendance(selectedid);

        for(int i=0;i<dateCalendarList.size();i++){
            if(attendance==dateCalendarList.get(i)){
                dateCalendarList.remove(i);
                break;
            }
        }
        calendarView.unMarkDate(year,month,day);
    }

    public void editAttendance(final AttendanceStorage selectedattendance){
        selectedtimestamp=selectedattendance.getTimestamp();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit attendance");
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view= inflater.inflate(R.layout.edit_attendance, null);
        builder.setView(view);

        WheelView hourswheelview=view.findViewById(R.id.hours_wheelview);
        WheelView minuteswheelview=view.findViewById(R.id.minutes_wheelview);
        final String[] hours = getResources().getStringArray(R.array.hours);
        final String[] minutes=getResources().getStringArray(R.array.minutes);
        WheelViewAdapter hourswheelviewadapter=new ArrayWheelAdapter<>(this, hours);
        WheelViewAdapter minuteswheelviewadapter=new ArrayWheelAdapter<>(this, minutes);
        hourswheelview.setAdapter(hourswheelviewadapter);
        minuteswheelview.setAdapter(minuteswheelviewadapter);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat presethour=new SimpleDateFormat("HH");
        SimpleDateFormat presetminute=new SimpleDateFormat("mm");
        TimestampAdjuster ta=new TimestampAdjuster();
        selectedattendancehour=ta.getHour(selectedtimestamp);
        selectedattendanceminute=ta.getMinute(selectedtimestamp);
        final String currentdate=ta.getYear(selectedtimestamp)+"-"+ta.getMonth(selectedtimestamp)+"-"+ta.getDate(selectedtimestamp);
        int hour = Integer.parseInt(selectedattendancehour);
        int minute = Integer.parseInt(selectedattendanceminute);
        hourswheelview.setCurrentItem(hour);
        minuteswheelview.setCurrentItem(minute);
        hourswheelview.setCyclic(true);
        minuteswheelview.setCyclic(true);
        hourswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendancehour=hours[newValue];
            }
        });

        minuteswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendanceminute=minutes[newValue];
            }
        });

        final EditText editattendancename=(EditText) view.findViewById(R.id.editattendance_name);

        editattendancename.setText(selectedattendance.getName());


        builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {

                String customtimestamp=currentdate + " " + selectedattendancehour + ":" + selectedattendanceminute;
                new AttendanceHelper(CalendarHistory.this).editAttendance(selectedattendance.getId(),customtimestamp,editattendancename.getText().toString());

                for(int i=0;i<dateCalendarList.size();i++){
                    if(selectedattendance==dateCalendarList.get(i)){
                        dateCalendarList.set(i,new AttendanceStorage(selectedattendance.getId(),customtimestamp,selectedattendance.getName()));
                        break;
                    }
                }

                calendarView.unMarkDate(Integer.parseInt(new TimestampAdjuster().getYear(selectedattendance.getTimestamp())),Integer.parseInt(new TimestampAdjuster().getMonth(selectedattendance.getTimestamp())),Integer.parseInt(new TimestampAdjuster().getDay(selectedattendance.getTimestamp())));
                calendarView.markDate(Integer.parseInt(new TimestampAdjuster().getYear(customtimestamp)),Integer.parseInt(new TimestampAdjuster().getMonth(customtimestamp)),Integer.parseInt(new TimestampAdjuster().getDay(customtimestamp)));
            }
        });

        builder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });

        builder.show();
    }
}