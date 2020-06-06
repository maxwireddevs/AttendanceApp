package com.wireddevs.attendanceapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.AttendanceHelper;
import com.wireddevs.attendanceapp.database.DatabaseHelper;
import com.wireddevs.attendanceapp.database.model.Student;
import com.yanzhenjie.wheel.OnWheelChangedListener;
import com.yanzhenjie.wheel.WheelView;
import com.yanzhenjie.wheel.adapters.ArrayWheelAdapter;
import com.yanzhenjie.wheel.adapters.WheelViewAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfile extends AppCompatActivity {

    private String studentlanguage;
    private Student viewablestudent;
    private float selectedduration=2;
    private String selectedattendancehour;
    private String selectedattendanceminute;
    private String currentdate,customtimestamp;
    private long passableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile);

        Intent intent=getIntent();
        passableID=intent.getLongExtra("passableID",0);

        Button addAttendance = (Button) findViewById(R.id.addattendance_button);
        Button editProfile = (Button) findViewById(R.id.editstudent_button);
        Button deleteProfile = (Button) findViewById(R.id.deletestudent_button);
        Button attendanceViewer = (Button) findViewById(R.id.attendancehistorystudent_button);

        DatabaseHelper db = new DatabaseHelper(this);
        viewablestudent=db.getStudent(passableID);

        textSetHelper(viewablestudent.getName());

        addAttendance.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggleAttendanceButton(viewablestudent.getName());
            }
        });
        editProfile.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editStudent(viewablestudent);
            }
        });
        deleteProfile.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){ deleteStudent(viewablestudent.getId(), viewablestudent.getName());}});
        attendanceViewer.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toCalendarHistory();
            }
        });
    }
    public void textSetHelper(String name){
        TextView profilenametext = (TextView) findViewById(R.id.profilename_text);
        profilenametext.setText(name);
    }
    public void setSelectedDuration(int progress){
        selectedduration=(float)progress/2;
    }
    public void toggleAttendanceButton(final String studentname){
        selectedduration=2;
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentProfile.this);
        builder.setTitle(R.string.attendancebuilder_title);
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View view= inflater.inflate(R.layout.add_attendance_dialog, null);
        builder.setView(view);
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat currenthour=new SimpleDateFormat("HH");
        SimpleDateFormat currentminute=new SimpleDateFormat("mm");
        selectedattendancehour=currenthour.format(c.getTime());
        selectedattendanceminute=currentminute.format(c.getTime());
        currentdate=sdf.format(c.getTime());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        WheelView hourswheelview=view.findViewById(R.id.hours_wheelview);
        WheelView minuteswheelview=view.findViewById(R.id.minutes_wheelview);
        final String[] hours = getResources().getStringArray(R.array.hours);
        final String[] minutes=getResources().getStringArray(R.array.minutes);
        WheelViewAdapter hourswheelviewadapter=new ArrayWheelAdapter<>(this, hours);
        WheelViewAdapter minuteswheelviewadapter=new ArrayWheelAdapter<>(this, minutes);
        hourswheelview.setAdapter(hourswheelviewadapter);
        minuteswheelview.setAdapter(minuteswheelviewadapter);
        hourswheelview.setCurrentItem(hour);
        minuteswheelview.setCurrentItem(minute);
        hourswheelview.setCyclic(true);
        minuteswheelview.setCyclic(true);
        hourswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendancehour=hours[newValue];
            }
        });

        minuteswheelview.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                selectedattendanceminute=minutes[newValue];
            }
        });

        builder.setPositiveButton("Add attendance",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                customtimestamp=currentdate + " " + selectedattendancehour + ":" + selectedattendanceminute;
                addAttendanceMethod(customtimestamp,studentname,selectedduration);
            }
        });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addAttendanceMethod(String timestamp, String studentname, float studentduration){
        AttendanceHelper db = new AttendanceHelper(this);
        long id = db.forceAttendance(timestamp,studentname);
        if(id!=-1){
            Toast.makeText(StudentProfile.this,"Attendance Added", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(StudentProfile.this,"Process failed, try again", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteStudent(final long ID, final String name){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Delete person and all attendance history?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(StudentProfile.this);
                db.deleteStudent(ID);
                AttendanceHelper ah = new AttendanceHelper(StudentProfile.this);
                ah.deleteAllAttendance(name);
                Toast.makeText(StudentProfile.this,"Person deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Delete person but keep attendance history", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(StudentProfile.this);
                db.deleteStudent(ID);
                Toast.makeText(StudentProfile.this,"Person deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });
        builder.show();
    }

    public void toCalendarHistory(){
        Intent intent = new Intent(this, CalendarHistory.class);
        intent.putExtra("passableID",this.passableID);
        startActivity(intent);
    }

    public void editStudent(Student student){
        final long selectedStudentID=student.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentProfile.this);
        builder.setTitle("Edit student");
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view= inflater.inflate(R.layout.edit_student, null);
        builder.setView(view);
        final EditText editstudentname=(EditText)view.findViewById(R.id.editstudent_name);
        int position=0;
        editstudentname.setText(student.getName());
        builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Student studentToReturn=new Student();
                int duplicatecount=0;
                DatabaseHelper dh=new DatabaseHelper(StudentProfile.this);
                Cursor res=dh.getAllData();
                while(res.moveToNext()){
                    if (editstudentname.getText().toString().equalsIgnoreCase(res.getString(2))) {
                        duplicatecount=duplicatecount+1;
                    }
                }
                if(duplicatecount>1){
                    Toast.makeText(StudentProfile.this,"Person name must be unique", Toast.LENGTH_SHORT).show();
                }

                else if(duplicatecount<2) {
                    if (editstudentname.length() != 0) {

                        studentToReturn.setName(editstudentname.getText().toString());

                        AttendanceHelper ah=new AttendanceHelper(StudentProfile.this);
                        res=ah.getAllData();

                        while(res.moveToNext()){
                            if(res.getString(2).equals(viewablestudent.getName())){
                                ah.editAttendance(res.getLong(0),res.getString(1),editstudentname.getText().toString());
                            }
                        }
                        viewablestudent.setName(editstudentname.getText().toString());

                        dh.editStudent(selectedStudentID, studentToReturn);

                        Toast.makeText(StudentProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();

                        textSetHelper(viewablestudent.getName());
                    } else {
                        Toast.makeText(StudentProfile.this, "Cannot be null", Toast.LENGTH_SHORT).show();
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

}


