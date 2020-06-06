package com.wireddevs.attendanceapp.view;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.DatabaseHelper;
import com.wireddevs.attendanceapp.database.model.Student;
import com.wireddevs.attendanceapp.utils.RecyclerViewItemClickListener;
import com.wireddevs.attendanceapp.utils.ShadowVerticalSpaceItemDecorator;
import com.wireddevs.attendanceapp.ViewStudentsRecyclerView.ViewStudentRecyclerViewAdapter;
import com.wireddevs.attendanceapp.utils.VerticalSpaceItemDecorator;
import java.util.Collections;
import java.util.Comparator;

public class ViewStudents extends Fragment{
    private EditText searchForThisKeyword;
    private String searchKeyword=null;
    private long passableID;
    private static RecyclerView studentrecyclerview;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_view_students, container,false);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        // you can add listener of elements here
        ImageView banner = (ImageView) view.findViewById(R.id.bannerforviewstudents);
        Drawable bannerforviewstudents = banner.getDrawable();
        bannerforviewstudents.setAlpha(100);

        studentrecyclerview=(RecyclerView) view.findViewById(R.id.student_recyclerview);
        studentrecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator =
                new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator =
                new ShadowVerticalSpaceItemDecorator(getActivity(), R.drawable.drop_shadow);
        studentrecyclerview.setLayoutManager(layoutManager);
        studentrecyclerview.addItemDecoration(shadowItemDecorator);
        studentrecyclerview.addItemDecoration(itemDecorator);

        searchForThisKeyword = (EditText) view.findViewById(R.id.searchkeyword);

        studentrecyclerview.setAdapter(((MainTabs)ViewStudents.this.getActivity()).getAdapter());

        studentrecyclerview.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), studentrecyclerview ,new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        passableID=((MainTabs)ViewStudents.this.getActivity()).getSearchResult().get(position).getId();
                        toStudentProfile();
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );

        searchForThisKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchKeyword = searchForThisKeyword.getText().toString();
                SQLSearch(searchKeyword);
                ((MainTabs)ViewStudents.this.getActivity()).getAdapter().notifyDataSetChanged();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void SQLSearch(final String searchKeyword) {
        ((MainTabs)this.getActivity()).getSearchResult().clear();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        Cursor res = db.getAllData();
        while (res.moveToNext()) {
            if (res.getString(2).toLowerCase().contains(searchKeyword.toLowerCase())) {
                Student studentFound=new Student();
                studentFound.setId(res.getInt(0));
                studentFound.setTimestamp(res.getString(1));
                studentFound.setName(res.getString(2));
                ((MainTabs)ViewStudents.this.getActivity()).getSearchResult().add(studentFound);
            }
        }
        Collections.sort(((MainTabs)ViewStudents.this.getActivity()).getSearchResult(), new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

    }

    private void toStudentProfile(){
        Intent intent = new Intent(getActivity(), StudentProfile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("passableID",this.passableID);
        startActivity(intent);
    }
}
