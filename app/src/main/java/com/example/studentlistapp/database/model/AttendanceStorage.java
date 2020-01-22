package com.example.studentlistapp.database.model;

public class AttendanceStorage {

    public static final String TABLE_NAME = "attendancelist";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_SESSION_DURATION = "duration";

    private long id;
    private String timestamp;
    private String name;
    private float duration;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT (datetime('now','localtime')),"
                    + COLUMN_STUDENT_NAME + " TEXT,"
                    + COLUMN_STUDENT_SESSION_DURATION + " REAL"
                    + ")";

    public AttendanceStorage() {
    }

    public AttendanceStorage(long id, String timestamp, String name, float duration) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDuration() { return duration; }

    public void setDuration(float duration) { this.duration = duration; }

}
