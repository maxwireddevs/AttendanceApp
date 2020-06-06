package com.wireddevs.attendanceapp.ViewStudentsRecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.model.Student;

import androidx.recyclerview.widget.RecyclerView;

public class ViewStudentRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView studentname;


    private Student student;

    public ViewStudentRecyclerViewHolder(Context context,View itemView){
        super(itemView);
        this.studentname=(TextView)itemView.findViewById(R.id.student_name_recyclerview_item);
        itemView.setOnClickListener(this);
    }

    public void bindStudent(Student student){
        this.student=student;


        this.studentname.setText(student.getName());
    }

    @Override
    public void onClick(View v) {
        if (this.student != null) {
            //TODO onClick student list item
        }
    }
}
