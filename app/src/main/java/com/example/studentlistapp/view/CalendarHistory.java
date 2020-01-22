package com.example.studentlistapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.studentlistapp.R;
import com.example.studentlistapp.database.AttendanceHelper;
import com.example.studentlistapp.database.DatabaseHelper;
import com.example.studentlistapp.database.model.AttendanceStorage;
import com.example.studentlistapp.database.model.Student;
import com.example.studentlistapp.utils.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Calendar;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

public class CalendarHistory extends AppCompatActivity {
    ArrayList<AttendanceStorage> dateCalendarList=new ArrayList<>();
    private TextView attendancecounteralltime, attendancecounterthismonth;
    private float totalattendance;
    private float totalattendancethismonth=0;
    private MCalendarView calendarView;
    final TimestampAdjuster adjusttimestamp=new TimestampAdjuster();
    private int currentmonth;
    private float selectedduration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_history);

        Intent intent=getIntent();
        long passableID = intent.getLongExtra("passableID", 0);

        calendarView = ((MCalendarView) findViewById(R.id.calendar));
        attendancecounteralltime=(TextView) findViewById(R.id.attendance_counter_alltime);
        attendancecounterthismonth=(TextView) findViewById(R.id.attendance_counter_thismonth);

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
                    dateCalendarList.add(new AttendanceStorage(res.getInt(0),res.getString(1),res.getString(2),res.getFloat(3)));
                    totalattendance = totalattendance + res.getFloat(3);
                }
            }
        }

        TimestampAdjuster ta=new TimestampAdjuster();

        for(int i=0;i<dateCalendarList.size();i++){
            calendarView.markDate(Integer.parseInt(ta.getYear(dateCalendarList.get(i).getTimestamp())), Integer.parseInt(ta.getMonth(dateCalendarList.get(i).getTimestamp())), Integer.parseInt(ta.getDay(dateCalendarList.get(i).getTimestamp())));
        }

        currentmonth=Calendar.getInstance().get(Calendar.MONTH)+1;

        calendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                currentmonth=month;
                totalAttendanceForMonthAdjuster(month,year);
            }
        });

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {


                toggleProfileAttendanceButton(selectedstudent.getName(),date.getYear(),date.getMonth(),date.getDay());
            }
        });


        String attendancecountdisplay="Total attendance of all time: "+totalattendance+" hours";
        attendancecounteralltime.setText(attendancecountdisplay);

        totalAttendanceForMonthAdjuster(Calendar.getInstance().get(Calendar.MONTH)+1,Calendar.getInstance().get(Calendar.YEAR));

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
            final float selectedduration=selectedattendance.getDuration();

            if(year==Integer.parseInt(adjusttimestamp.getYear(selectedtimestamp))){
                if(month==Integer.parseInt(adjusttimestamp.getMonth(selectedtimestamp))) {
                    if (day==Integer.parseInt(adjusttimestamp.getDay(selectedtimestamp))) {
                        if(name.equalsIgnoreCase(selectedname)) {
                            buffer.append("Date: " + adjusttimestamp.getDay(selectedtimestamp) + " " + adjusttimestamp.getStringMonth(selectedtimestamp) + " " + adjusttimestamp.getYear(selectedtimestamp) + "\n");
                            buffer.append("Time: " + adjusttimestamp.getHour(selectedtimestamp) + ":" + adjusttimestamp.getMinute(selectedtimestamp) + "\n");
                            buffer.append("Name: " + selectedname + "\n");
                            buffer.append("Duration: " + selectedduration + " hour(s)" + "\n" + "\n");
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

    public void totalAttendanceForMonthAdjuster(int month, int year){
        totalattendancethismonth=0;
        for(int i=0;i<dateCalendarList.size();i++) {
            if(year==Integer.parseInt(adjusttimestamp.getYear(dateCalendarList.get(i).getTimestamp())))
            if (month == Integer.parseInt(adjusttimestamp.getMonth(dateCalendarList.get(i).getTimestamp()))) {
                totalattendancethismonth = totalattendancethismonth + dateCalendarList.get(i).getDuration();
            }
        }
        String attendancecountpermonthdisplay = "Total attendance for this month: " + totalattendancethismonth + " hours";
        attendancecounterthismonth.setText(attendancecountpermonthdisplay);
    }

    public void removeAttendance(AttendanceStorage attendance,int year,int month,int day){
        final long selectedid=attendance.getId();
        final String selectedtimestamp=attendance.getTimestamp();
        String selectedname=attendance.getName();
        final float selecteddurationtodelete=attendance.getDuration();

        AttendanceHelper ah=new AttendanceHelper(CalendarHistory.this);
        ah.deleteThisAttendance(selectedid);

        for(int i=0;i<dateCalendarList.size();i++){
            if(attendance==dateCalendarList.get(i)){
                dateCalendarList.remove(i);
                break;
            }
        }

        calendarView.unMarkDate(year,month,day);

        totalattendance=totalattendance-selecteddurationtodelete;
        String attendancecountdisplay="Total attendance of all time: "+totalattendance+" hours";
        attendancecounteralltime.setText(attendancecountdisplay);

        if(Integer.parseInt(adjusttimestamp.getMonth(selectedtimestamp))==currentmonth){
            totalattendancethismonth=totalattendancethismonth-selecteddurationtodelete;
            String attendancecountpermonthdisplay = "Total attendance on this month: " + totalattendancethismonth + " hours";
            attendancecounterthismonth.setText(attendancecountpermonthdisplay);
        }
    }

    public void editAttendance(final AttendanceStorage selectedattendance){
        selectedduration=selectedattendance.getDuration();
        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarHistory.this);
        builder.setTitle("Edit attendance");
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view= inflater.inflate(R.layout.edit_attendance, null);
        builder.setView(view);

        final TextView showhourstextview=(TextView) view.findViewById(R.id.edithours_textview);
        final EditText editattendancename=(EditText) view.findViewById(R.id.editattendance_name);
        final EditText editattendancetimestamp=(EditText) view.findViewById(R.id.editattendance_timestamp);
        final SeekBar editattendanceduration=(SeekBar) view.findViewById(R.id.editattendance_duration);

        String hourstextviewadjuster="Duration: "+selectedattendance.getDuration()+" hours";
        showhourstextview.setText(hourstextviewadjuster);

        editattendancename.setText(selectedattendance.getName());
        editattendancetimestamp.setText(selectedattendance.getTimestamp());
        editattendanceduration.setProgress(Math.round(selectedattendance.getDuration()*2));

        editattendanceduration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedduration=(float)progress/2;
                String hourstextviewadjuster="Duration: "+selectedduration+" hours";
                showhourstextview.setText(hourstextviewadjuster);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {

                String updatedtimestamp=editattendancetimestamp.getText().toString();
                new AttendanceHelper(CalendarHistory.this).editAttendance(selectedattendance.getId(),updatedtimestamp,editattendancename.getText().toString(),selectedduration);

                for(int i=0;i<dateCalendarList.size();i++){
                    if(selectedattendance==dateCalendarList.get(i)){
                        dateCalendarList.set(i,new AttendanceStorage(selectedattendance.getId(),updatedtimestamp,selectedattendance.getName(),selectedduration));
                        break;
                    }
                }

                calendarView.unMarkDate(Integer.parseInt(new TimestampAdjuster().getYear(selectedattendance.getTimestamp())),Integer.parseInt(new TimestampAdjuster().getMonth(selectedattendance.getTimestamp())),Integer.parseInt(new TimestampAdjuster().getDay(selectedattendance.getTimestamp())));
                calendarView.markDate(Integer.parseInt(new TimestampAdjuster().getYear(updatedtimestamp)),Integer.parseInt(new TimestampAdjuster().getMonth(updatedtimestamp)),Integer.parseInt(new TimestampAdjuster().getDay(updatedtimestamp)));

                totalattendance=totalattendance-selectedattendance.getDuration()+selectedduration;
                String attendancecountdisplay="Total attendance of all time: "+totalattendance+" hours";
                attendancecounteralltime.setText(attendancecountdisplay);

                if(Integer.parseInt(adjusttimestamp.getMonth(selectedattendance.getTimestamp()))== currentmonth) {
                    if (Integer.parseInt(adjusttimestamp.getMonth(updatedtimestamp)) != currentmonth){
                        totalattendancethismonth = totalattendancethismonth - selectedattendance.getDuration();
                        String attendancecountpermonthdisplay = "Total attendance on this month: " + totalattendancethismonth + " hours";
                        attendancecounterthismonth.setText(attendancecountpermonthdisplay);
                    }
                    else if(Integer.parseInt(adjusttimestamp.getMonth(updatedtimestamp)) == currentmonth){
                        totalattendancethismonth = totalattendancethismonth - selectedattendance.getDuration()+selectedduration;
                        String attendancecountpermonthdisplay = "Total attendance on this month: " + totalattendancethismonth + " hours";
                        attendancecounterthismonth.setText(attendancecountpermonthdisplay);
                    }
                }
                else if(Integer.parseInt(adjusttimestamp.getMonth(selectedattendance.getTimestamp()))!= currentmonth){
                    if (Integer.parseInt(adjusttimestamp.getMonth(updatedtimestamp)) != currentmonth){
                        String attendancecountpermonthdisplay = "Total attendance on this month: " + totalattendancethismonth + " hours";
                        attendancecounterthismonth.setText(attendancecountpermonthdisplay);
                    }
                    else if(Integer.parseInt(adjusttimestamp.getMonth(updatedtimestamp)) == currentmonth){
                        totalattendancethismonth = totalattendancethismonth+selectedduration;
                        String attendancecountpermonthdisplay = "Total attendance on this month: " + totalattendancethismonth + " hours";
                        attendancecounterthismonth.setText(attendancecountpermonthdisplay);
                    }
                }
            }
        });

        builder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });

        builder.show();
    }

    public void updateAttendance(){

    }
}