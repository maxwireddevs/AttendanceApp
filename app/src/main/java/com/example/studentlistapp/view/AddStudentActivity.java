package com.example.studentlistapp.view;

import androidx.fragment.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.studentlistapp.R;
import com.example.studentlistapp.database.DatabaseHelper;

public class AddStudentActivity extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText studentnametext;
    private EditText studentnicknametext;
    private EditText studentgradetext;
    private EditText studentphonetext;
    private String studentlanguage;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_student, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
        setHasOptionsMenu(true);
        studentnametext = (EditText) view.findViewById(R.id.addstudent_name);
        studentnicknametext= (EditText) view.findViewById(R.id.addstudent_nickname);
        studentgradetext= (EditText) view.findViewById(R.id.addstudent_grade);
        studentphonetext = (EditText) view.findViewById(R.id.addstudent_phone);
        Button savebutton = (Button) view.findViewById(R.id.addstudent_savebutton);

        Spinner spinnerlanguage = (Spinner) view.findViewById(R.id.addstudent_language);
        spinnerlanguage.setOnItemSelectedListener(this);

        savebutton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(studentnametext.length()!=0&&studentnicknametext.length()!=0&&studentgradetext.length()!=0&&studentlanguage!=null&&studentphonetext.length()!=0){
                    boolean existDuplicate=false;
                    DatabaseHelper dh=new DatabaseHelper(getActivity());
                    Cursor res=dh.getAllData();
                    while(res.moveToNext()){
                        if (studentnametext.getText().toString().equalsIgnoreCase(res.getString(2))) {
                            existDuplicate=true;
                        }
                    }
                    if(existDuplicate){
                        Toast.makeText(getActivity(),"Student name must be unique", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addStudentMethod(studentnametext.getText().toString(), studentnicknametext.getText().toString(), studentgradetext.getText().toString(), studentlanguage, studentphonetext.getText().toString());
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



    private void addStudentMethod(String studentname, String studentnickname, String studentgrade, String studentlanguage, String studentphone){
        DatabaseHelper db = new DatabaseHelper(getActivity());
        long id = db.insertStudent(studentname,studentnickname, studentgrade, studentlanguage,studentphone);
        if(id!=-1){
            Toast.makeText(getActivity(),"Student Added", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(),"Process failed, try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if((parent.getItemAtPosition(pos).toString()).equals("Indonesian")){
            studentlanguage="Indonesian";
        }

        else if((parent.getItemAtPosition(pos).toString()).equals("English")){
            studentlanguage="English";
        }
        else if((parent.getItemAtPosition(pos).toString()).equals("Select Language")){
            studentlanguage=null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


}
