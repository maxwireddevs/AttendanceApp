package com.example.studentlistapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.studentlistapp.database.model.AttendanceStorage;
import com.example.studentlistapp.view.AttendanceHandler;

import static com.example.studentlistapp.database.model.AttendanceStorage.COLUMN_TIMESTAMP;
import static com.example.studentlistapp.database.model.AttendanceStorage.TABLE_NAME;

public class AttendanceHelper extends SQLiteOpenHelper{

    //Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "attendancelist_db";

    public AttendanceHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(AttendanceStorage.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(String name, float attendance) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(AttendanceStorage.COLUMN_STUDENT_NAME, name);
        values.put(AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION, attendance);

        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public void overwriteAttendance(long id, String timestamp, String name, float duration){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AttendanceStorage.COLUMN_ID, id);
        values.put(AttendanceStorage.COLUMN_TIMESTAMP, timestamp);
        values.put(AttendanceStorage.COLUMN_STUDENT_NAME, name);
        values.put(AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION, duration);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public long forceAttendance(String timestamp, String name, float duration){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(AttendanceStorage.COLUMN_TIMESTAMP, timestamp);
        values.put(AttendanceStorage.COLUMN_STUDENT_NAME, name);
        values.put(AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION, duration);
        long id=db.insert(TABLE_NAME,null,values);
        db.close();
        return id;
    }

    public int getAttendanceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteAllAttendance(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, AttendanceStorage.COLUMN_STUDENT_NAME + " = ?",
                new String[]{String.valueOf(name)});
        db.close();
    }

    public void deleteThisAttendance(long ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, AttendanceStorage.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }

    public Cursor getAllDataSortedByTimestamp(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+COLUMN_TIMESTAMP+" DESC",null);
        return res;
    }
    public AttendanceStorage getAttendance(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(AttendanceStorage.TABLE_NAME,
                new String[]{AttendanceStorage.COLUMN_ID, AttendanceStorage.COLUMN_TIMESTAMP, AttendanceStorage.COLUMN_STUDENT_NAME, AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION},
                AttendanceStorage.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            // prepare note object
            AttendanceStorage attendance = new AttendanceStorage(
                    cursor.getInt(cursor.getColumnIndex(AttendanceStorage.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(AttendanceStorage.COLUMN_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndex(AttendanceStorage.COLUMN_STUDENT_NAME)),
                    cursor.getFloat(cursor.getColumnIndex(AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION)));
            // close the db connection
            cursor.close();
            db.close();
            return attendance;
        }
        else{
            return null;
        }
    }

    public void editAttendance(long ID, String timestamp, String name, float duration){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(AttendanceStorage.COLUMN_TIMESTAMP,timestamp);
        values.put(AttendanceStorage.COLUMN_STUDENT_NAME,name);
        values.put(AttendanceStorage.COLUMN_STUDENT_SESSION_DURATION,duration);

        db.update(AttendanceStorage.TABLE_NAME,values,AttendanceStorage.COLUMN_ID + " = ? ", new String[]{String.valueOf(ID)});
        db.close();
    }

}
