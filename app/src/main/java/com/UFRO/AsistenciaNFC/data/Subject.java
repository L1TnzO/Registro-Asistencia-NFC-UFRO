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
    private String subjectTheoricalDays;
    private String subjectPracticalDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LinkedHashMap<LocalDate, Boolean> theoreticalAttendanceData;
    private LinkedHashMap<LocalDate, Boolean> practicalAttendanceData;


    public Subject(String subjectCode, String subjectName, String subjectTheoricalDays, String subjectPracticalDays, LocalTime subjectTimeStart, LocalTime subjectTimeEnd,LocalDate startDate, LocalDate endDate) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.subjectTimeStart = subjectTimeStart;
        this.subjectTimeEnd = subjectTimeEnd;
        this.subjectTheoricalDays = subjectTheoricalDays;
        this.subjectPracticalDays = subjectPracticalDays;

        this.startDate = startDate;
        this.endDate = endDate;
        this.theoreticalAttendanceData = new LinkedHashMap<>();
        this.practicalAttendanceData = new LinkedHashMap<>();
    }

    public float calculateAttendance() {
        float totalTheoreticalClasses = theoreticalAttendanceData.size();
        float attendedTheoreticalClasses = 0;
        for (Boolean attended : theoreticalAttendanceData.values()) {
            if (attended) attendedTheoreticalClasses++;
        }
        float totalPracticalClasses = practicalAttendanceData.size();
        float attendedPracticalClasses = 0;
        for (Boolean attended : practicalAttendanceData.values()) {
            if (attended) attendedPracticalClasses++;
        }
        return ((attendedTheoreticalClasses / totalTheoreticalClasses) * 100 + (attendedPracticalClasses / totalPracticalClasses) * 100) / 2;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectTheoricalDays() {
        return subjectTheoricalDays;
    }

    public String getSubjectPracticalDays() {
        return subjectPracticalDays;
    }

    public LocalTime getSubjectTimeStart() {
        return subjectTimeStart;
    }

    public LocalTime getSubjectTimeEnd() {
        return subjectTimeEnd;
    }

    public LocalDate getStartDate() {return startDate;}

    public LocalDate getEndDate() {return endDate;}

    public HashMap<LocalDate, Boolean> getTheoreticalAttendanceData() {
        return theoreticalAttendanceData;
    }

    public HashMap<LocalDate, Boolean> getPracticalAttendanceData() {
        return practicalAttendanceData;
    }

    public void setTheoreticalAttendanceData(LinkedHashMap<LocalDate, Boolean> theoreticalAttendanceData) {
        this.theoreticalAttendanceData = theoreticalAttendanceData;
    }

    public void setPracticalAttendanceData(LinkedHashMap<LocalDate, Boolean> practicalAttendanceData) {
        this.practicalAttendanceData = practicalAttendanceData;
    }
}

