package com.example.studentlistapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.studentlistapp.R;
import com.example.studentlistapp.database.AttendanceHelper;
import com.example.studentlistapp.database.DatabaseHelper;
import com.example.studentlistapp.database.model.AttendanceStorage;
import com.example.studentlistapp.database.model.Student;
import com.example.studentlistapp.utils.TimestampAdjuster;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AttendanceHandlerGraphView extends Fragment {

    private Date latestDateToBeDisplayed;
    private GraphView attendanceGraphView;
    private SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<AttendanceStorage> attendanceStorageArrayList=new ArrayList<>();
    private long passableID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_attendance_handler_graph_view, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here

        latestDateToBeDisplayed=Calendar.getInstance().getTime();

        Button nextweekbutton = view.findViewById(R.id.next_button);
        Button previousweekbutton = view.findViewById(R.id.previous_button);

        attendanceGraphView=new GraphView(getActivity());
        attendanceGraphView=view.findViewById(R.id.attendancegraph);

        attendanceGraphView.getViewport().setXAxisBoundsManual(true);
        attendanceGraphView.getViewport().setMinX(-0);
        attendanceGraphView.getViewport().setMaxX(6);

        attendanceGraphView.getViewport().setYAxisBoundsManual(true);
        attendanceGraphView.getViewport().setMinY(0);
        attendanceGraphView.getViewport().setMaxY(60);

        setupGraph(formatter.format(latestDateToBeDisplayed));

        nextweekbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestDateToBeDisplayed=new Date(latestDateToBeDisplayed.getTime()+7*86400000);
                setupGraph(formatter.format(latestDateToBeDisplayed));
            }
        });

        previousweekbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestDateToBeDisplayed=new Date(latestDateToBeDisplayed.getTime()-7*86400000);
                setupGraph(formatter.format(latestDateToBeDisplayed));
            }
        });
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupGraph(String timestamp){
        attendanceStorageArrayList.clear();
        attendanceGraphView.removeAllSeries();
        DataPoint[] attendanceDataPoints=new DataPoint[7];
        Integer[] totalStudentsForEachDay=new Integer[7];

        for(int i=0;i<7;i++){
            totalStudentsForEachDay[i]=0;
        }

        AttendanceHelper ah=new AttendanceHelper(getActivity());
        Cursor res=ah.getAllDataSortedByTimestamp();

        int lastposition=0;

        while(res.moveToNext()){
            boolean breakWhileLoop=false;
            if(new TimestampAdjuster().getDate(res.getString(1)).equals(timestamp)){
                lastposition=res.getPosition();
                breakWhileLoop=true;
            }
            if(breakWhileLoop){
                break;
            }
        }

        res.moveToPosition(lastposition-1);

        while(res.moveToNext()){
            boolean stopWhileLoop=true;
            for (int j=0; j<7; j++) {
                if (new TimestampAdjuster().getDate(res.getString(1)).equals(backwardDaysCounter(j, timestamp))){
                    attendanceStorageArrayList.add(new AttendanceStorage(res.getInt(0),res.getString(1),res.getString(2),res.getFloat(3)));
                    totalStudentsForEachDay[j]=totalStudentsForEachDay[j]+1;
                    stopWhileLoop=false;
                }
            }
            if(stopWhileLoop){
                break;
            }
        }

        for(int i=0;i<7;i++){
            attendanceDataPoints[i]=new DataPoint(i,totalStudentsForEachDay[6-i]);
        }

        String[] xAxisLabelArray=weekLongTimestampMaker(formatter.format(latestDateToBeDisplayed));
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(attendanceGraphView);
        staticLabelsFormatter.setHorizontalLabels(xAxisLabelArray);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(attendanceDataPoints);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                toggleAttendanceDate(dataPoint);
            }
        });
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(getResources().getColor(R.color.black));
        series.setSpacing(25);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, 100, 100);
            }
        });

        attendanceGraphView.addSeries(series);
        attendanceGraphView.getGridLabelRenderer().setTextSize(20f);
        attendanceGraphView.getGridLabelRenderer().setNumHorizontalLabels(7);
        attendanceGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    private String backwardDaysCounter(int howmanydays,String currentdaytimestamp){
        String timestampresult;

        long d1=inMillis(currentdaytimestamp);
        long d2=d1-86400000*howmanydays;

        timestampresult=formatter.format(new Date(d2));
        return timestampresult;
    }

    private String[] weekLongTimestampMaker(String currenttimestamp){
        String[] weekLongTimestamp=new String[7];
        for(int i=0;i<7;i++){
            String adjuster=backwardDaysCounter(6-i,currenttimestamp);
            weekLongTimestamp[i]=new TimestampAdjuster().getDay(adjuster)+"/"+new TimestampAdjuster().getMonth(adjuster);
        }
        return weekLongTimestamp;
    }

    private long inMillis(String timestamp){

        Date parsetimestamp= null;
        try {
            parsetimestamp = formatter.parse(new TimestampAdjuster().getDate(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert parsetimestamp != null;
        return parsetimestamp.getTime();
    }

    private void toggleAttendanceDate(DataPointInterface dataPoint){

        int index=(int)dataPoint.getX();
        String currentdate=backwardDaysCounter(6-index,new TimestampAdjuster().getDate(formatter.format(latestDateToBeDisplayed)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View view= inflater.inflate(R.layout.attendance_graph_dialog, null);

        TextView attendanceDialogNumber = view.findViewById(R.id.attendancegraph_title);
        ListView attendanceListView = view.findViewById(R.id.attendancegraph_listview);

        String dialognumberadjuster="Number of students: " + (int)dataPoint.getY();
        attendanceDialogNumber.setText(dialognumberadjuster);

        final ArrayList<String> stuffToShow=new ArrayList<>();
        final ArrayList<String> nameToShow=new ArrayList<>();

        stuffToShow.clear();
        nameToShow.clear();

        for(int i=0;i<attendanceStorageArrayList.size();i++){
            if(new TimestampAdjuster().getDate(attendanceStorageArrayList.get(i).getTimestamp()).equals(currentdate)){
                nameToShow.add(attendanceStorageArrayList.get(i).getName());
                stuffToShow.add(attendanceStorageArrayList.get(i).getName()+" ("+attendanceStorageArrayList.get(i).getDuration()+" hours)");
            }
        }

        Collections.sort(stuffToShow, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        Collections.sort(nameToShow, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,stuffToShow);
        attendanceListView.setAdapter(adapter);
        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                DatabaseHelper dh=new DatabaseHelper(getActivity());
                Student selectedstudent=dh.getStudentByName(nameToShow.get(position));
                passableID=selectedstudent.getId();
                toStudentProfile();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void toStudentProfile(){
        Intent intent = new Intent(getActivity(), StudentProfile.class);
        intent.putExtra("passableID",this.passableID);
        startActivity(intent);
    }
}