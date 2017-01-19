package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 1/18/2017.
 */
public class PatientAssistant {
    String name;
    String lastname;
    int hour;
    int min;
    int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFullName(){
        return name.concat(" ").concat(lastname);
    }

    public String getTime() {
        int endHour = getHour() + getDuration() / 60;
        int endMinute = getMin() + getDuration() % 60;
        return String.valueOf(getMin()) + " : "
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
        setMin(startMinute);
        setDuration(((endHour - startHour) * 60) + ((endMinute - startMinute)));
    }
}
