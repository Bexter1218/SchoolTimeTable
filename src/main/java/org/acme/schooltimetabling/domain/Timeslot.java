package org.acme.schooltimetabling.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record Timeslot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        String day = "";
        int number = 0;

        switch (dayOfWeek){
            case MONDAY -> day = "H";
            case TUESDAY -> day = "K";
            case WEDNESDAY -> day = "Sz";
            case THURSDAY -> day = "Cs";
            case FRIDAY -> day = "P";
        }

        switch (startTime().getHour()){
            case 7 -> number = 1;
            case 8 -> number = 2;
            case 9 -> number = 3;
            case 10 -> number = 4;
            case 11 -> number = 5;
            case 12 -> number = 6;
            case 13 -> number = 7;
            case 14 -> number = 8;
            case 15 -> number = 9;
        }
        return day + "/" + number;
    }

}