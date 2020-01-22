package com.example.studentlistapp.database.model;

public class Student {
    public static final String TABLE_NAME = "studentlist";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_NICKNAME = "nickname";
    public static final String COLUMN_STUDENT_GRADE = "grade";
    public static final String COLUMN_STUDENT_LANGUAGE = "language";
    public static final String COLUMN_STUDENT_PHONE = "phone";

    private long id;
    private String timestamp;
    public String name;
    private String nickname;
    private String grade;
    private String language;
    private String phone;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT (datetime('now','localtime')),"
                    + COLUMN_STUDENT_NAME + " TEXT,"
                    + COLUMN_STUDENT_NICKNAME + " TEXT,"
                    + COLUMN_STUDENT_GRADE + " TEXT,"
                    + COLUMN_STUDENT_LANGUAGE +" TEXT,"
                    + COLUMN_STUDENT_PHONE +" TEXT"
                    + ")";

    public Student() {
    }

    public Student(long id, String timestamp, String name, String nickname, String grade, String language, String phone) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.nickname = nickname;
        this.grade = grade;
        this.language=language;
        this.phone=phone;
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

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String nickname) { this.nickname = nickname; }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) { this.grade = grade; }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

