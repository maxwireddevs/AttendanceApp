package com.example.studentlistapp.ViewStudentsRecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.studentlistapp.R;
import com.example.studentlistapp.database.model.Student;

import androidx.recyclerview.widget.RecyclerView;

public class ViewStudentRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView studentname;
    private final TextView studentgrade;
    private final TextView studentnickname;

    private Student student;

    public ViewStudentRecyclerViewHolder(Context context,View itemView){
        super(itemView);
        this.studentname=(TextView)itemView.findViewById(R.id.student_name_recyclerview_item);
        this.studentgrade=(TextView)itemView.findViewById(R.id.student_grade_recyclerview_item);
        this.studentnickname=(TextView)itemView.findViewById(R.id.student_nickname_recyclerview_item);
        itemView.setOnClickListener(this);
    }

    public void bindStudent(Student student){
        this.student=student;

        String studentnicknameadjust="Nickname: "+student.getNickName();
        String studentgradeadjust="Grade: "+student.getGrade();

        this.studentname.setText(student.getName());
        this.studentnickname.setText(studentnicknameadjust);
        this.studentgrade.setText(studentgradeadjust);
    }

    @Override
    public void onClick(View v) {
        if (this.student != null) {
            //TODO onClick student list item
        }
    }
}
