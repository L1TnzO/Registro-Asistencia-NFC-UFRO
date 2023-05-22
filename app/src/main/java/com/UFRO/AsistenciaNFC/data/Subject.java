package com.UFRO.AsistenciaNFC.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Subject {

    private String subjectCode;
    private String subjectName;
    private LocalTime subjectTimeStart;
    private LocalTime subjectTimeEnd;
    private String subjectDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LinkedHashMap<LocalDate, Boolean> attendanceData;


    public Subject(String subjectCode, String subjectName, String subjectDays, LocalTime subjectTimeStart, LocalTime subjectTimeEnd,LocalDate startDate, LocalDate endDate) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.subjectTimeStart = subjectTimeStart;
        this.subjectTimeEnd = subjectTimeEnd;
        this.subjectDays = subjectDays;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attendanceData = new LinkedHashMap<>();
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

    public LocalDate getStartDate() {return startDate;}

    public LocalDate getEndDate() {return endDate;}

    public HashMap<LocalDate, Boolean> getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(LinkedHashMap<LocalDate, Boolean> attendanceData) {
        this.attendanceData = attendanceData;
    }
}

