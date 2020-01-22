package com.example.studentlistapp.view;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import com.example.studentlistapp.R;
import com.example.studentlistapp.database.DatabaseHelper;
import com.example.studentlistapp.database.model.Student;
import com.example.studentlistapp.utils.RecyclerViewItemClickListener;
import com.example.studentlistapp.utils.ShadowVerticalSpaceItemDecorator;
import com.example.studentlistapp.ViewStudentsRecyclerView.ViewStudentRecyclerViewAdapter;
import com.example.studentlistapp.utils.VerticalSpaceItemDecorator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewStudents extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText searchForThisKeyword;
    private ArrayList<Student> searchResult=new ArrayList<>();
    private String searchKeyword=null;
    private int selectedColumn;
    private long passableID;
    private RecyclerView studentrecyclerview;


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

        DatabaseHelper db = new DatabaseHelper(getActivity());
        Cursor res = db.getAllData();

        while (res.moveToNext()) {
            Student studentFound=new Student();
            studentFound.setId(res.getInt(0));
            studentFound.setTimestamp(res.getString(1));
            studentFound.setName(res.getString(2));
            studentFound.setNickName(res.getString(3));
            studentFound.setGrade(res.getString(4));
            studentFound.setLanguage(res.getString(5));
            studentFound.setPhone(res.getString(6));
            searchResult.add(studentFound);
        }

        Collections.sort(searchResult, new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

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
        Spinner spinnerForSearch = (Spinner) view.findViewById(R.id.searchspinner);
        spinnerForSearch.setOnItemSelectedListener(this);

        ViewStudentRecyclerViewAdapter adapter = new ViewStudentRecyclerViewAdapter(getActivity(), R.layout.list_item_students_view, searchResult);

        studentrecyclerview.setAdapter(adapter);

        studentrecyclerview.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), studentrecyclerview ,new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        passableID=searchResult.get(position).getId();
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
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void SQLSearch(final String searchKeyword) {
        searchResult.clear();

        DatabaseHelper db = new DatabaseHelper(getActivity());
        Cursor res = db.getAllData();

        while (res.moveToNext()) {
            if (res.getString(selectedColumn).toLowerCase().contains(searchKeyword.toLowerCase())) {
                Student studentFound=new Student();
                studentFound.setId(res.getInt(0));
                studentFound.setTimestamp(res.getString(1));
                studentFound.setName(res.getString(2));
                studentFound.setNickName(res.getString(3));
                studentFound.setGrade(res.getString(4));
                studentFound.setLanguage(res.getString(5));
                studentFound.setPhone(res.getString(6));
                searchResult.add(studentFound);
            }
        }

        Collections.sort(searchResult, new Comparator<Student>() {
            @Override
            public int compare(Student lhs, Student rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        ViewStudentRecyclerViewAdapter adapter=new ViewStudentRecyclerViewAdapter(getActivity(),R.layout.list_item_students_view,searchResult);

        studentrecyclerview.setAdapter(adapter);

        studentrecyclerview.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), studentrecyclerview ,new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        passableID=searchResult.get(position).getId();
                        toStudentProfile();
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if ((parent.getItemAtPosition(pos).toString()).equals("Name")) {
            selectedColumn = 2;
        } else if ((parent.getItemAtPosition(pos).toString()).equals("Nickname")) {
            selectedColumn = 3;
        } else if ((parent.getItemAtPosition(pos).toString()).equals("Phone Number")) {
            selectedColumn = 6;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    private void toStudentProfile(){
        Intent intent = new Intent(getActivity(), StudentProfile.class);
        intent.putExtra("passableID",this.passableID);
        startActivity(intent);
    }
}
