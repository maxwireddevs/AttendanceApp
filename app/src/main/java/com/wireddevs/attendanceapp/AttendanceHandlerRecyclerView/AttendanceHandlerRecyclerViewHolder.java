package com.wireddevs.attendanceapp.AttendanceHandlerRecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.model.AttendanceStorage;
import com.wireddevs.attendanceapp.utils.TimestampAdjuster;

import androidx.recyclerview.widget.RecyclerView;

public class AttendanceHandlerRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView attendancestudentname;
    private final TextView attendancestudentdate;
    private final TextView attendancestudenttime;

    private AttendanceStorage attendanceStorage;
    private Context context;

    public AttendanceHandlerRecyclerViewHolder(Context context,View itemView){
        super(itemView);
        this.context = context;
        this.attendancestudentname=(TextView)itemView.findViewById(R.id.student_name_attendancerecyclerview_item);
        this.attendancestudentdate=(TextView)itemView.findViewById(R.id.student_date_attendancerecyclerview_item);
        this.attendancestudenttime=(TextView)itemView.findViewById(R.id.student_time_attendancerecyclerview_item);
        itemView.setOnClickListener(this);
    }

    public void bindAttendance(AttendanceStorage attendanceStorage){
        TimestampAdjuster ta=new TimestampAdjuster();
        this.attendanceStorage=attendanceStorage;

        String studentdateadjust="Date: "+ ta.getDay(attendanceStorage.getTimestamp()) + " " + ta.getStringMonth(attendanceStorage.getTimestamp()) + " " + ta.getYear(attendanceStorage.getTimestamp());
        String studenttimeadjust="Time: " + ta.getHour(attendanceStorage.getTimestamp()) + ":" + ta.getMinute(attendanceStorage.getTimestamp());

        this.attendancestudentname.setText(attendanceStorage.getName());
        this.attendancestudentdate.setText(studentdateadjust);
        this.attendancestudenttime.setText(studenttimeadjust);
    }

    @Override
    public void onClick(View v) {
        if (this.attendanceStorage != null) {
            //TODO onClick attendance list item
        }
    }



}

