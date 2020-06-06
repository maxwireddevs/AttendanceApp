package com.wireddevs.attendanceapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.wireddevs.attendanceapp.database.model.Student;

import static com.wireddevs.attendanceapp.database.model.Student.TABLE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "studentlist_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Student.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(String name) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Student.COLUMN_STUDENT_NAME, name);


        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public void overwriteStudent(long id, String timestamp, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_ID, id);
        values.put(Student.COLUMN_TIMESTAMP, timestamp);
        values.put(Student.COLUMN_STUDENT_NAME, name);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getStudentCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void deleteStudent(long ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Student.COLUMN_ID + " = ?",
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

    public Student getStudent(long id) {

        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_TIMESTAMP, Student.COLUMN_STUDENT_NAME},
                Student.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Student student=new Student(
                    cursor.getLong(cursor.getColumnIndex(Student.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_NAME)));
            // close the db connection
            cursor.close();
            return student;
        }
        else{
            return null;
        }
    }

    public void editStudent(long ID,Student newStudent){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(Student.COLUMN_STUDENT_NAME,newStudent.getName());


        db.update(Student.TABLE_NAME,values,Student.COLUMN_ID + " = ? ", new String[]{String.valueOf(ID)});

        db.close();
    }

    public Student getStudentByName(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_TIMESTAMP, Student.COLUMN_STUDENT_NAME},
                Student.COLUMN_STUDENT_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            Student student = new Student(
                    cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_NAME)));
            // close the db connection
            cursor.close();
            db.close();
            return student;
        }
        else{
            return null;
        }
    }

}

