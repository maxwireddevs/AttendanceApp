package com.wireddevs.attendanceapp.utils;

public class TimestampAdjuster {

    private int timezoneadjuster=7;

    public String getYear(String timestamp){
        return timestamp.substring(0,4);
    }

    public String getStringMonth(String timestamp){
        String month=null;
        switch (timestamp.substring(5, 7)) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
        }
        return month;
    }

    public String getMonth(String timestamp){
        return timestamp.substring(5,7);
    }

    public String getDay(String timestamp){
        return timestamp.substring(8,10);
    }

    public String getHour(String timestamp){
        return timestamp.substring(11,13);
    }

    public String getMinute(String timestamp){
        return timestamp.substring(14,16);
    }

    public String getDate(String timestamp){return timestamp.substring(0,10);}

}
