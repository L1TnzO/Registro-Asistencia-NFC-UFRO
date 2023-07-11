package com.UFRO.AsistenciaNFC.data;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SubjectManager {

    private HashMap<String, Subject> subjects;
    private CsvReader csvReader;
    private CsvWriter csvWriter;
    private AttendanceManager attendanceManager;
    private String csvFilePath;

    public SubjectManager(String csvPath, CsvReader csvReader, CsvWriter csvWriter) throws IOException {
        this.csvFilePath = csvPath;
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
        this.subjects = csvReader.readCsvData(csvPath);

        // Create the current date and time
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Pass them to the AttendanceManager constructor
        this.attendanceManager = new AttendanceManager(this, currentDate, currentTime);

        for (Subject subject : subjects.values()) {
            generateClassDates(subject);
        }
    }

    public HashMap<String, Subject> getSubjects() {
        return subjects;
    }

    public Subject getSubject(String subjectCode) {
        return subjects.get(subjectCode);
    }

    public void saveSubjects() throws IOException {
        csvWriter.writeSubjects(csvFilePath, subjects, attendanceManager);
    }

    public AttendanceManager getAttendanceManager() {
        return this.attendanceManager;
    }

    void generateClassDates(Subject subject) {
        LocalDate currentDate = subject.getStartDate();
        LinkedHashMap<LocalDate, Boolean> theoreticalAttendanceData = new LinkedHashMap<>(subject.getTheoreticalAttendanceData());
        LinkedHashMap<LocalDate, Boolean> practicalAttendanceData = new LinkedHashMap<>(subject.getPracticalAttendanceData());

        while (!currentDate.isAfter(subject.getEndDate())) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();

            if (subject.getSubjectTheoricalDays().charAt(dayOfWeek - 1) == '1') {
                Boolean existingValue = theoreticalAttendanceData.get(currentDate);
                if (existingValue == null) {
                    theoreticalAttendanceData.put(currentDate, false);
                }
            } else if (subject.getSubjectPracticalDays().charAt(dayOfWeek - 1) == '1') {
                Boolean existingValue = practicalAttendanceData.get(currentDate);
                if (existingValue == null) {
                    practicalAttendanceData.put(currentDate, false);
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        subject.setTheoreticalAttendanceData(theoreticalAttendanceData);
        subject.setPracticalAttendanceData(practicalAttendanceData);
    }



}

