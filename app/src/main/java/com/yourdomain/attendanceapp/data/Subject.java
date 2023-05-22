package com.yourdomain.attendanceapp.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class Subject {

    private String subjectCode;
    private String subjectName;
    private String subjectDays;
    private LocalTime subjectTimeStart;
    private LocalTime subjectTimeEnd;
    private HashMap<LocalDate, Boolean> attendanceData;

    public Subject(String subjectCode, String subjectName, String subjectDays, LocalTime subjectTimeStart, LocalTime subjectTimeEnd) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.subjectDays = subjectDays;
        this.subjectTimeStart = subjectTimeStart;
        this.subjectTimeEnd = subjectTimeEnd;
        this.attendanceData = new HashMap<>();
    }

    public float calculateAttendance() {
        float totalClasses = attendanceData.size();
        float attendedClasses = 0;
        for (Boolean attended : attendanceData.values()) {
            if (attended) attendedClasses++;
        }
        return (attendedClasses / totalClasses) * 100;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectDays() {
        return subjectDays;
    }

    public LocalTime getSubjectTimeStart() {
        return subjectTimeStart;
    }

    public LocalTime getSubjectTimeEnd() {
        return subjectTimeEnd;
    }

    public HashMap<LocalDate, Boolean> getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(HashMap<LocalDate, Boolean> attendanceData) {
        this.attendanceData = attendanceData;
    }
}

