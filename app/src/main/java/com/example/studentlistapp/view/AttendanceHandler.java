package com.example.studentlistapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.studentlistapp.BuildConfig;
import com.example.studentlistapp.R;
import com.example.studentlistapp.AttendanceHandlerRecyclerView.AttendanceHandlerRecyclerViewAdapter;
import com.example.studentlistapp.database.AttendanceHelper;
import com.example.studentlistapp.database.DatabaseHelper;
import com.example.studentlistapp.database.model.AttendanceStorage;
import com.example.studentlistapp.database.model.Student;
import com.example.studentlistapp.utils.RecyclerViewItemClickListener;
import com.example.studentlistapp.utils.ShadowVerticalSpaceItemDecorator;
import com.example.studentlistapp.utils.TimestampAdjuster;
import com.example.studentlistapp.utils.VerticalSpaceItemDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AttendanceHandler extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView attendancerecyclerview;
    private AttendanceHandlerRecyclerViewAdapter adapter;
    private ArrayList<AttendanceStorage> studentList = new ArrayList<AttendanceStorage>();
    private long passableID;
    private Spinner attendancelogsorter;
    private float selectedduration;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_attendance_handler, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
        setHasOptionsMenu(true);
        ImageView banner = (ImageView) view.findViewById(R.id.bannerforattendancehandler);
        Drawable bannerforviewstudents = banner.getDrawable();
        bannerforviewstudents.setAlpha(100);

        attendancelogsorter = (Spinner) view.findViewById(R.id.attendancelogspinner);
        attendancelogsorter.setOnItemSelectedListener(this);

        attendancerecyclerview=(RecyclerView) view.findViewById(R.id.attendance_recyclerview);
        attendancerecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator =
                new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator =
                new ShadowVerticalSpaceItemDecorator(getActivity(), R.drawable.drop_shadow);
        attendancerecyclerview.setLayoutManager(layoutManager);
        attendancerecyclerview.addItemDecoration(shadowItemDecorator);
        attendancerecyclerview.addItemDecoration(itemDecorator);

        FloatingActionButton fabsendreport = (FloatingActionButton) view.findViewById(R.id.fab_send_report);

        fabsendreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSendReportButton(getActivity());
            }
        });
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void toggleStudentAttendance(final int position, final long ID, final String timestamp, final String name, final float duration, final Context fileContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(fileContext);
        builder.setCancelable(true);

        TimestampAdjuster adjusttimestamp = new TimestampAdjuster();

        StringBuffer buffer = new StringBuffer();
        buffer.append("Date: ").append(adjusttimestamp.getDay(timestamp)).append(" ").append(adjusttimestamp.getStringMonth(timestamp)).append(" ").append(adjusttimestamp.getYear(timestamp)).append("\n");
        buffer.append("Time: ").append(adjusttimestamp.getHour(timestamp)).append(":").append(adjusttimestamp.getMinute(timestamp)).append("\n");
        buffer.append("Name: ").append(name).append("\n");
        buffer.append("Duration: ").append(duration).append("\n");

        builder.setMessage(buffer);

        builder.setPositiveButton("Edit attendance", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                editAttendance(new AttendanceStorage(ID, timestamp, name, duration));
            }
        });

        builder.setNegativeButton("Open student", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                DatabaseHelper dh=new DatabaseHelper(getActivity());
                Student selectedstudent=dh.getStudentByName(name);
                passableID=selectedstudent.getId();
                toStudentProfile();
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {
                deleteThisAttendance(ID,position);
            }
        });

        builder.show();
    }

    private void deleteThisAttendance(long ID, int position){
        AttendanceHelper ah = new AttendanceHelper(getActivity());
        ah.deleteThisAttendance(ID);
        studentList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getSelectedItemPosition()==0){
            attendancestoragedisplayerInput(0);
        }
        else if(parent.getSelectedItemPosition()==1){
            attendancestoragedisplayerInput(1);
        }

        else if(parent.getSelectedItemPosition()==2){
            attendancestoragedisplayerInput(6);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void attendancestoragedisplayerInput(int spinneroption){
        AttendanceHelper ah = new AttendanceHelper(getActivity());
        Cursor res=ah.getAllDataSortedByTimestamp();
        studentList.clear();

        while(res.moveToNext()){
            boolean breakWhileLoop=true;
            if(daydifference(res.getString(1))<=spinneroption){
                studentList.add(ah.getAttendance(res.getInt(0)));
                breakWhileLoop=false;
            }
            if(breakWhileLoop){
                break;
            }
        }

        adapter=new AttendanceHandlerRecyclerViewAdapter(getActivity(),R.layout.list_item_attendance_view,studentList);
        attendancerecyclerview.setAdapter(adapter);

        attendancerecyclerview.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), attendancerecyclerview ,new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        toggleStudentAttendance(position,studentList.get(position).getId(),studentList.get(position).getTimestamp(),studentList.get(position).getName(),studentList.get(position).getDuration(),getActivity());
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );
    }

    private int daydifference(String timestamp){

        try {
            long d1 = inMillis(timestamp);
            long d2 = new Date().getTime();

            long daysbetween=d2-d1;

            return (int)((double)daysbetween/86400000);

        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }
    }

    private long inMillis(String timestamp){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date parsetimestamp= null;
        try {
            parsetimestamp = formatter.parse(new TimestampAdjuster().getDate(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsetimestamp.getTime();
    }

    private void toggleSendReportButton(Context context){
        new SendReportCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        File directoryReport=new File(context.getApplicationContext().getFilesDir()+"/report");
        String filename = "AttendanceReport.csv";
        File sharer = new File(directoryReport, filename);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", sharer);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share CSV"));
    }

    public class SendReportCSVTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        AttendanceHelper attendancehelper;
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
            attendancehelper = new AttendanceHelper(getActivity());
        }

        protected Boolean doInBackground(final String... args) {
            try {

                int rowcount = 0;
                int colcount = 0;

                String filename = "AttendanceReport.csv";
                File directoryReport=new File(getActivity().getApplicationContext().getFilesDir()+"/report");
                directoryReport.mkdir();
                File saveFile = new File(directoryReport,filename);

                FileWriter fw = new FileWriter(saveFile);

                BufferedWriter bw = new BufferedWriter(fw);
                rowcount = studentList.size();
                colcount = 4;
                bw.write("id,timestamp,name,duration");
                bw.newLine();
                if (rowcount > 0) {
                    for (int i = 0; i < rowcount; i++) {
                        bw.write(studentList.get(i).getId() + ","+studentList.get(i).getTimestamp()+","+studentList.get(i).getName()+","+studentList.get(i).getDuration());
                        bw.newLine();
                    }

                    bw.flush();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(getActivity(), "Export successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toStudentProfile(){
        Intent intent = new Intent(getActivity(), StudentProfile.class);
        intent.putExtra("passableID",this.passableID);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        attendancelogsorter.setSelection(0);
    }

    public void editAttendance(final AttendanceStorage selectedattendance){
        selectedduration=selectedattendance.getDuration();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit attendance");
        builder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view= inflater.inflate(R.layout.edit_attendance, null);
        builder.setView(view);

        final TextView showhourstextview=(TextView) view.findViewById(R.id.edithours_textview);
        final EditText editattendancename=(EditText) view.findViewById(R.id.editattendance_name);
        final EditText editattendancetimestamp=(EditText) view.findViewById(R.id.editattendance_timestamp);
        final SeekBar editattendanceduration=(SeekBar) view.findViewById(R.id.editattendance_duration);

        String hourstextviewadjuster="Duration: "+selectedattendance.getDuration()+" hours";
        showhourstextview.setText(hourstextviewadjuster);

        editattendancename.setText(selectedattendance.getName());
        editattendancetimestamp.setText(selectedattendance.getTimestamp());
        editattendanceduration.setProgress(Math.round(selectedattendance.getDuration()*2));

        editattendanceduration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedduration=(float)progress/2;
                String hourstextviewadjuster="Duration: "+selectedduration+" hours";
                showhourstextview.setText(hourstextviewadjuster);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogBox, int id) {

                String updatedtimestamp=editattendancetimestamp.getText().toString();
                new AttendanceHelper(getActivity()).editAttendance(selectedattendance.getId(),updatedtimestamp,editattendancename.getText().toString(),selectedduration);

                for(int i=0;i<studentList.size();i++){
                    if(selectedattendance==studentList.get(i)){
                        studentList.set(i,new AttendanceStorage(selectedattendance.getId(),updatedtimestamp,selectedattendance.getName(),selectedduration));
                        adapter.notifyItemChanged(i);
                        break;
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