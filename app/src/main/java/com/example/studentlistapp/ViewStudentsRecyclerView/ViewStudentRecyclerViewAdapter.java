package com.example.studentlistapp.ViewStudentsRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentlistapp.database.model.Student;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ViewStudentRecyclerViewAdapter extends RecyclerView.Adapter<ViewStudentRecyclerViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    // 1. Initialize our adapter
    private final List<Student> students;
    private Context context;
    private int itemResource;

    public ViewStudentRecyclerViewAdapter(Context context, int itemResource, List<Student> students){
        this.students=students;
        this.context=context;
        this.itemResource=itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public ViewStudentRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new ViewStudentRecyclerViewHolder(this.context,view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(ViewStudentRecyclerViewHolder holder, int position) {
        // 5. Use position to access the correct Student object
        Student student=this.students.get(position);
        holder.bindStudent(student);
    }

    @Override
    public int getItemCount() {
        return this.students.size();
    }

    @Override
    public String getSectionName(int position) {
        return Character.toString(students.get(position).getName().charAt(0));
    }

}
