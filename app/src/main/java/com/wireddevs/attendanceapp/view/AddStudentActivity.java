package com.wireddevs.attendanceapp.view;

import androidx.fragment.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.DatabaseHelper;

public class AddStudentActivity extends Fragment {
    private EditText studentnametext;
    private String studentlanguage;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_student, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
        setHasOptionsMenu(true);
        studentnametext = (EditText) view.findViewById(R.id.addstudent_name);
        Button savebutton = (Button) view.findViewById(R.id.addstudent_savebutton);


        savebutton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(studentnametext.length()!=0){
                    boolean existDuplicate=false;
                    DatabaseHelper dh=new DatabaseHelper(getActivity());
                    Cursor res=dh.getAllData();
                    while(res.moveToNext()){
                        if (studentnametext.getText().toString().equalsIgnoreCase(res.getString(2))) {
                            existDuplicate=true;
                        }
                    }
                    if(existDuplicate){
                        Toast.makeText(getActivity(),"Name must be unique", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addStudentMethod(studentnametext.getText().toString());
                    }

                }

                else{
                    Toast.makeText(getActivity(),"Fill in the necessary form", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    private void addStudentMethod(String studentname){
        DatabaseHelper db = new DatabaseHelper(getActivity());
        long id = db.insertStudent(studentname);
        if(id!=-1){
            Toast.makeText(getActivity(),"***Instance added, continue adding instance or refresh app to see the change.***", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(),"Process failed, try again", Toast.LENGTH_SHORT).show();
        }
    }






}
