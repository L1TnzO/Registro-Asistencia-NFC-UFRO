package com.UFRO.AsistenciaNFC.data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;

public class AttendanceManager {

    private SubjectManager subjectManager;
    private LocalDate currentDate;
    private LocalTime currentTime;

    public AttendanceManager(SubjectManager subjectManager, LocalDate currentDate, LocalTime currentTime) {
        this.subjectManager = subjectManager;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public void updateAttendance(String nfcCode) throws IOException {

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
}


