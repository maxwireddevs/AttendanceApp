package com.wireddevs.attendanceapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.wireddevs.attendanceapp.BuildConfig;
import com.wireddevs.attendanceapp.R;
import com.wireddevs.attendanceapp.database.AttendanceHelper;
import com.wireddevs.attendanceapp.database.DatabaseHelper;
import com.wireddevs.attendanceapp.database.model.AttendanceStorage;
import com.wireddevs.attendanceapp.database.model.Student;
import com.wireddevs.attendanceapp.utils.TimestampAdjuster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Fragment implements AdapterView.OnItemSelectedListener {

    private int FILE_SEARCH_CODE = 1;
    private int REPORT_SEARCH_CODE=2;
    private ArrayList<String> csvReader=new ArrayList<>();
    private ArrayList<String> csvReportReader=new ArrayList<>();
    private ViewPager viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
        Button toViewStudentsButton = (Button) view.findViewById(R.id.tostudentview_button);
        Button toAttendanceHandlerButton = (Button) view.findViewById(R.id.toattendancehandler_button);
        Button toAddStudentButton = (Button) view.findViewById(R.id.toaddstudent_button);
        Button importCSVButton = (Button) view.findViewById(R.id.import_csv_button);
        Button exportCSVButton = (Button) view.findViewById(R.id.export_csv_button);
        viewPager=(ViewPager) getActivity().findViewById(R.id.view_pager);
        toViewStudentsButton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewPager.setCurrentItem(1);
            }
        });
        toAttendanceHandlerButton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewPager.setCurrentItem(3);
            }
        });
        toAddStudentButton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.attendancebuilder_title);
                builder.setCancelable(true);
                LayoutInflater inflater = getLayoutInflater();
                View view= inflater.inflate(R.layout.add_student_dialog, null);
                builder.setView(view);
                final EditText studentnametext=(EditText)view.findViewById(R.id.addstudent_name);
                Button addstudentbutton=(Button)view.findViewById(R.id.addstudent_savebutton);
                addstudentbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                ((MainTabs)MainActivity.this.getActivity()).refreshSearchResult();
                                ((MainTabs)MainActivity.this.getActivity()).getAdapter().notifyDataSetChanged();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(),"Fill in the necessary form", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
        importCSVButton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                importCSV();
            }
        });

        exportCSVButton.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v){
                exportToCsv(v,getActivity());
            }
        });
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void exportToCsv(View v, final Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setItems(R.array.exportcsv, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exportCsvMethod(which,context);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exportCsvMethod(int choice, Context context){
        if(choice==0){
            new ExportParticularsCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            File directoryBackup=new File(context.getApplicationContext().getFilesDir()+"/backup");
            String filename = "UpdateParticulars.csv";
            File sharer = new File(directoryBackup, filename);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", sharer);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share CSV"));
        }
        else if(choice==1){
            new ExportAttendanceCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            File directoryBackup=new File(context.getApplicationContext().getFilesDir()+"/backup");
            String filename = "UpdateAttendance.csv";
            File sharer = new File(directoryBackup, filename);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", sharer);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share CSV"));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ExportAttendanceCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        AttendanceHelper attendancehelper;
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
            attendancehelper = new AttendanceHelper(getActivity());
        }

        protected Boolean doInBackground(final String... args) {
            Cursor c = null;
            try {
                c = attendancehelper.getAllData();
                int rowcount = 0;
                int colcount = 0;
                String filename = "UpdateAttendance.csv";
                File directoryBackup=new File(getActivity().getFilesDir()+"/backup");
                directoryBackup.mkdir();
                File saveFile = new File(directoryBackup,filename);

                FileWriter fw = new FileWriter(saveFile);

                BufferedWriter bw = new BufferedWriter(fw);
                rowcount = attendancehelper.getAttendanceCount();
                colcount = c.getColumnCount();
                if (rowcount > 0) {
                    c.moveToFirst();
                    for (int i = 0; i < colcount; i++) {
                        if (i != colcount - 1) {
                            bw.write(c.getColumnName(i) + ",");
                        } else {
                            bw.write(c.getColumnName(i));
                        }
                    }
                    bw.newLine();
                    for (int i = 0; i < rowcount; i++) {
                        c.moveToPosition(i);
                        for (int j = 0; j < colcount; j++) {
                            if (j != colcount - 1)
                                bw.write(c.getString(j) + ",");
                            else
                                bw.write(c.getString(j));
                        }
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

    public class ExportParticularsCSVTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        DatabaseHelper databasehelper;
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
            databasehelper = new DatabaseHelper(getActivity());
        }

        protected Boolean doInBackground(final String... args) {
            Cursor c = null;
            try {
                c = databasehelper.getAllData();

                int rowcount = 0;
                int colcount = 0;

                String filename = "UpdateParticulars.csv";
                File directoryBackup=new File(getActivity().getApplicationContext().getFilesDir()+"/backup");
                directoryBackup.mkdir();
                File saveFile = new File(directoryBackup,filename);

                FileWriter fw = new FileWriter(saveFile);

                BufferedWriter bw = new BufferedWriter(fw);
                rowcount = databasehelper.getStudentCount();
                colcount = c.getColumnCount();
                if (rowcount > 0) {
                    c.moveToFirst();

                    for (int i = 0; i < colcount; i++) {
                        if (i != colcount - 1) {

                            bw.write(c.getColumnName(i) + ",");

                        } else {

                            bw.write(c.getColumnName(i));

                        }
                    }
                    bw.newLine();

                    for (int i = 0; i < rowcount; i++) {
                        c.moveToPosition(i);

                        for (int j = 0; j < colcount; j++) {
                            if (j != colcount - 1)
                                bw.write(c.getString(j) + ",");
                            else
                                bw.write(c.getString(j));
                        }
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


    private void importCSV(){
        performFileSearch();
    }

    private void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SEARCH_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == FILE_SEARCH_CODE) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                // Get the URI that points to the selected csv
                Uri csvUri = resultIntent.getData();
                csvReader.clear();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(csvUri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    String mLine;
                    while ((mLine = r.readLine()) != null) {
                        csvReader.add(mLine);
                    }
                    importOptionsDialog(csvReader);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
                }

            }
        }

        else if (requestCode == REPORT_SEARCH_CODE) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                // Get the URI that points to the selected csv
                Uri csvUri = resultIntent.getData();
                csvReportReader.clear();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(csvUri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    String mLine;
                    while ((mLine = r.readLine()) != null) {
                        csvReportReader.add(mLine);
                    }
                    importOptionsDialogForReport(csvReportReader);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private Student stringStudentSplitter(String intocolumns){
        ArrayList<String> splittedString=new ArrayList<>(Arrays.asList(intocolumns.split(",")));
        Student returnStudent=new Student();
        returnStudent.setId(Integer.parseInt(splittedString.get(0)));
        returnStudent.setTimestamp(splittedString.get(1));
        returnStudent.setName(splittedString.get(2));
        return returnStudent;
    }

    private AttendanceStorage stringAttendanceSplitter(String intocolumns){
        ArrayList<String> splittedString=new ArrayList<>(Arrays.asList(intocolumns.split(",")));
        AttendanceStorage returnAttendance=new AttendanceStorage();
        returnAttendance.setId(Integer.parseInt(splittedString.get(0)));
        returnAttendance.setTimestamp(splittedString.get(1));
        returnAttendance.setName(splittedString.get(2));
        return returnAttendance;
    }

    private void importOptionsDialog(final ArrayList readableCsv){
        if(readableCsv.get(0).equals("id,timestamp,name")) {
            AlertDialog.Builder particularsbuilder=new AlertDialog.Builder(getActivity());
            particularsbuilder.setCancelable(true);
            particularsbuilder.setTitle("Instance particulars database detected");
            particularsbuilder.setMessage("Proceed with updating the database? All previous data will be lost.");
            particularsbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final ProgressDialog progressDialog = new ProgressDialog(MainTabs.context);
                    progressDialog.setTitle("Updating particulars");
                    progressDialog.setMessage("Loading... This may take a while");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            DatabaseHelper dh=new DatabaseHelper(getActivity());
                            dh.deleteAllData();
                            for (int i = 1; i < readableCsv.size(); i++) {
                                dh.overwriteStudent(stringStudentSplitter(readableCsv.get(i).toString()).getId(), stringStudentSplitter(readableCsv.get(i).toString()).getTimestamp(), stringStudentSplitter(readableCsv.get(i).toString()).getName());
                            }
                            progressDialog.dismiss();
                        }
                    }).start();
                }
            });

            particularsbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            particularsbuilder.show();
        }
        else if(readableCsv.get(0).equals("id,timestamp,name,duration")){
            AlertDialog.Builder attendancebuilder=new AlertDialog.Builder(getActivity());
            attendancebuilder.setCancelable(true);
            attendancebuilder.setTitle("Attendance database detected");
            attendancebuilder.setMessage("Proceed with updating the database? All previous data will be lost.");
            attendancebuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final ProgressDialog progressDialog = new ProgressDialog(MainTabs.context);
                    progressDialog.setTitle("Updating attendance");
                    progressDialog.setMessage("Loading... This may take a while");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {
                            AttendanceHelper ah=new AttendanceHelper(MainTabs.context);
                            ah.deleteAllData();
                            for (int i = 1; i < readableCsv.size(); i++) {
                                ah.overwriteAttendance(stringAttendanceSplitter(readableCsv.get(i).toString()).getId(), stringAttendanceSplitter(readableCsv.get(i).toString()).getTimestamp(), stringAttendanceSplitter(readableCsv.get(i).toString()).getName());
                            }
                            progressDialog.dismiss();
                        }
                    }).start();
                }
            });
            attendancebuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                            }
            });
            attendancebuilder.show();
        }

        else{
            Toast.makeText(getActivity(), "File unreadable, try again", Toast.LENGTH_SHORT).show();
        }
    }




    private void importOptionsDialogForReport(final ArrayList readableCsv){
        if(readableCsv.get(0).equals("id,timestamp,name,duration")){
            AlertDialog.Builder attendancebuilder=new AlertDialog.Builder(getActivity());
            attendancebuilder.setCancelable(true);
            attendancebuilder.setTitle("Attendance database detected");
            attendancebuilder.setMessage("Proceed with updating the database?");
            attendancebuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Updating attendance");
                    progressDialog.setMessage("Loading... This may take a while");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(new Runnable() {
                        public void run() {

                            for(int i=1;i<readableCsv.size();i++) {

                                boolean isSame = false;

                                String timestampFromCsv = stringAttendanceSplitter(readableCsv.get(i).toString()).getTimestamp();
                                String nameFromCsv = stringAttendanceSplitter(readableCsv.get(i).toString()).getName();

                                AttendanceHelper ah=new AttendanceHelper(getActivity());
                                Cursor res=ah.getAllData();
                                while (res.moveToNext()) {

                                    TimestampAdjuster ta = new TimestampAdjuster();

                                    String yearFromCsv = ta.getYear(timestampFromCsv);
                                    String monthFromCsv = ta.getMonth(timestampFromCsv);
                                    String dayFromCsv = ta.getDay(timestampFromCsv);

                                    String yearFromDatabase = ta.getYear(res.getString(1));
                                    String monthFromDatabase = ta.getMonth(res.getString(1));
                                    String dayFromDatabase = ta.getDay(res.getString(1));
                                    String nameFromDatabase = res.getString(2);

                                    if (yearFromCsv.equals(yearFromDatabase) && monthFromCsv.equals(monthFromDatabase) && dayFromCsv.equals(dayFromDatabase) && nameFromCsv.equals(nameFromDatabase)) {
                                        isSame = true;
                                    }
                                }

                                if (!isSame) {
                                    ah.forceAttendance(timestampFromCsv, nameFromCsv);
                                }
                            }

                            progressDialog.dismiss();
                        }
                    }).start();
                }
            });
            attendancebuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            attendancebuilder.show();
        }

        else{
            Toast.makeText(getActivity(), "File unreadable, try again", Toast.LENGTH_SHORT).show();
        }
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