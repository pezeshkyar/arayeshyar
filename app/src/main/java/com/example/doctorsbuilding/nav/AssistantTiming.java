package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 1/16/2017.
 */
public class AssistantTiming {
    private int id;
    private int assistantId;
    private char date;
    private int hour;
    private int minute;
    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(int assistantId) {
        this.assistantId = assistantId;
    }

    public char getDate() {
        return date;
    }

    public void setDate(char date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTime() {
        int endHour = getHour() + getDuration() / 60;
        int endMinute = getMinute() + getDuration() % 60;
        return String.valueOf(getMinute()) + " : "
                + String.valueOf(getHour()) + "  الی  " + String.valueOf(endMinute) + " : " + String.valueOf(endHour);
    }

    public void setTime(String start, String end){

        String[] startTime = start.split(":");
        String[] endTime = end.split(":");

        int startHour = Integer.parseInt(startTime[0]);
        int startMinute = Integer.parseInt(startTime[1]);

        int endHour = Integer.parseInt(endTime[0]);
        int endMinute = Integer.parseInt(endTime[1]);

        setHour(startHour);
        setMinute(startMinute);
        setDuration(((endHour - startHour) * 60) + ((endMinute - startMinute)));
    }

    public String getPersianDate() {
        String day = "";
        switch (date) {
            case '0':
                day = "شنبه";
                break;
            case '1':
                day = "یکشنبه";
                break;
            case '2':
                day = "دوشنبه";
                break;
            case '3':
                day = "سه شنبه";
                break;
            case '4':
                day = "چهارشنبه";
                break;
            case '5':
                day = "پنج شنبه";
                break;
            case '6':
                day = "جمعه";
                break;
        }
        return day;
    }
}
