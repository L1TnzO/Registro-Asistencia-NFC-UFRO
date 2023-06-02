package com.UFRO.AsistenciaNFC.data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceManager {

    private SubjectManager subjectManager;

    public AttendanceManager(SubjectManager subjectManager) {
        this.subjectManager = subjectManager;
    }

    public void updateAttendance(String nfcCode) throws IOException {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Iterate over the subjects to find the matching subject
        for (Subject subject : subjectManager.getSubjects().values()) {
            if (subject.getSubjectCode().equals(nfcCode)) {
                // Check if today is one of the subject's days
                int dayOfWeek = currentDate.getDayOfWeek().getValue();  // This returns 1 for Monday and 7 for Sunday
                if (subject.getSubjectTheoricalDays().charAt(dayOfWeek - 1) == '1' || subject.getSubjectPracticalDays().charAt(dayOfWeek - 1) == '1') {
                    // Check if current time is within the subject's start and end times
                    if (!currentTime.isBefore(subject.getSubjectTimeStart()) && !currentTime.isAfter(subject.getSubjectTimeEnd())) {
                        // Update attendance
                        LinkedHashMap<LocalDate, Boolean> updatedAttendance;
                        if (subject.getSubjectTheoricalDays().charAt(dayOfWeek - 1) == '1') {
                            updatedAttendance = new LinkedHashMap<>(subject.getTheoreticalAttendanceData());
                            updatedAttendance.put(currentDate, true);
                            subject.setTheoreticalAttendanceData(updatedAttendance);
                        } else {
                            updatedAttendance = new LinkedHashMap<>(subject.getPracticalAttendanceData());
                            updatedAttendance.put(currentDate, true);
                            subject.setPracticalAttendanceData(updatedAttendance);
                        }
                        subjectManager.saveSubjects();  // Save back to CSV file
                    }
                }
            }
        }
    }



    void generateClassDates(Subject subject) {
        LocalDate currentDate = subject.getStartDate();
        while (!currentDate.isAfter(subject.getEndDate())) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();
            if (subject.getSubjectTheoricalDays().charAt(dayOfWeek - 1) == '1') {
                LinkedHashMap<LocalDate, Boolean> theoreticalAttendanceData = new LinkedHashMap<>(subject.getTheoreticalAttendanceData());
                Boolean existingValue = theoreticalAttendanceData.get(currentDate);
                if (existingValue == null) {
                    theoreticalAttendanceData.put(currentDate, false);
                }
                subject.setTheoreticalAttendanceData(theoreticalAttendanceData);
            } else if (subject.getSubjectPracticalDays().charAt(dayOfWeek - 1) == '1') {
                LinkedHashMap<LocalDate, Boolean> practicalAttendanceData = new LinkedHashMap<>(subject.getPracticalAttendanceData());
                Boolean existingValue = practicalAttendanceData.get(currentDate);
                if (existingValue == null) {
                    practicalAttendanceData.put(currentDate, false);
                }
                subject.setPracticalAttendanceData(practicalAttendanceData);
            }
            currentDate = currentDate.plusDays(1);
        }
    }


    public SubjectManager getSubjectManager() {
        return subjectManager;
    }
}


