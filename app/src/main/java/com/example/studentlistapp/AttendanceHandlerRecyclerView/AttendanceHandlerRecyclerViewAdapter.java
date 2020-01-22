package com.example.studentlistapp.AttendanceHandlerRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentlistapp.database.model.AttendanceStorage;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AttendanceHandlerRecyclerViewAdapter extends RecyclerView.Adapter<AttendanceHandlerRecyclerViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    // 1. Initialize our adapter
    private final List<AttendanceStorage> attendanceList;
    private Context context;
    private int itemResource;

    public AttendanceHandlerRecyclerViewAdapter(Context context, int itemResource, List<AttendanceStorage> attendancelist){
        this.attendanceList=attendancelist;
        this.context=context;
        this.itemResource=itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public AttendanceHandlerRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new AttendanceHandlerRecyclerViewHolder(this.context,view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(AttendanceHandlerRecyclerViewHolder holder, int position) {
        // 5. Use position to access the correct Student object
        AttendanceStorage attendanceStorage=this.attendanceList.get(position);
        holder.bindAttendance(attendanceStorage);
    }

    @Override
    public int getItemCount() {
        return this.attendanceList.size();
    }

    @Override
    public String getSectionName(int position) {
        return Character.toString(attendanceList.get(position).getName().charAt(0));
    }

}
