package com.yourdomain.attendanceapp.data;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceManager {

    private HashMap<String, Subject> subjects;
    private final String csvFilePath;

    public AttendanceManager(String csvPath) throws IOException {
        this.csvFilePath = csvPath;
        subjects = new HashMap<>();
        loadCSVData(csvPath);
    }

    private void loadCSVData(String csvPath) throws IOException {
        File csvFile = new File(csvPath);
        if (csvFile.isFile()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                String subjectName = data[0];
                String subjectCode = data[1];
                LocalTime subjectTimeStart = LocalTime.parse(data[2], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime subjectTimeEnd = LocalTime.parse(data[3], DateTimeFormatter.ofPattern("HH:mm"));
                String subjectDays = data[4];
                HashMap<LocalDate, Boolean> attendanceData = parseAttendanceData(data[5]);

                Subject subject = new Subject(subjectCode, subjectName, subjectDays, subjectTimeStart, subjectTimeEnd);
                subject.setAttendanceData(attendanceData);
                subjects.put(subjectCode, subject);
            }
            csvReader.close();
        } else {
            throw new FileNotFoundException("CSV file not found.");
        }
    }

    private HashMap<LocalDate, Boolean> parseAttendanceData(String attendanceDataString) {
        HashMap<LocalDate, Boolean> attendanceData = new HashMap<>();
        String[] attendancePairs = attendanceDataString.split(",");
        for (String attendancePair : attendancePairs) {
            String[] pair = attendancePair.split(":");
            String dateString = pair[0].replace("\"", ""); // Remove surrounding quotes
            boolean isPresent = Boolean.parseBoolean(pair[1]);
            LocalDate date = LocalDate.parse(dateString);
            attendanceData.put(date, isPresent);
        }
        return attendanceData;
    }



    public float calculateOverallAttendance() {
        float totalClasses = 0;
        float attendedClasses = 0;
        for (Subject subject : subjects.values()) {
            totalClasses += subject.getAttendanceData().size();
            for (Boolean attended : subject.getAttendanceData().values()) {
                if (attended) attendedClasses++;
            }
        }
        return (attendedClasses / totalClasses) * 100;
    }

    public void saveSubjects() throws IOException {
        FileWriter csvWriter = new FileWriter(csvFilePath);
        for (Subject subject : subjects.values()) {
            List<String> row = new ArrayList<>();
            row.add(subject.getSubjectName());
            row.add(subject.getSubjectCode());
            row.add(subject.getSubjectTimeStart().toString());
            row.add(subject.getSubjectTimeEnd().toString());
            row.add(subject.getSubjectDays());

            StringBuilder attendanceData = new StringBuilder();
            for (Map.Entry<LocalDate, Boolean> entry : subject.getAttendanceData().entrySet()) {
                attendanceData.append(entry.getKey().toString())
                        .append(":")
                        .append(entry.getValue().toString())
                        .append(",");
            }

            if (attendanceData.length() > 0) {
                attendanceData.deleteCharAt(attendanceData.length() - 1); // remove the last comma
            }

            row.add("\"" + attendanceData.toString() + "\"");
            csvWriter.append(String.join(",", row));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }


    private String formatSubject(Subject subject) {
        StringBuilder sb = new StringBuilder();
        sb.append(subject.getSubjectName()).append(",");
        sb.append(subject.getSubjectCode()).append(",");
        sb.append(subject.getSubjectTimeStart().toString()).append(",");
        sb.append(subject.getSubjectTimeEnd().toString()).append(",");
        sb.append(subject.getSubjectDays()).append(",\"");
        for (Map.Entry<LocalDate, Boolean> entry : subject.getAttendanceData().entrySet()) {
            sb.append(entry.getKey().toString());
            sb.append(":");
            sb.append(entry.getValue().toString());
            sb.append(",");
        }
        if (!subject.getAttendanceData().isEmpty()) {
            sb.deleteCharAt(sb.length() - 1); // Remove last comma if there is attendance data
        }
        sb.append("\"");
        return sb.toString();
    }

    public HashMap<String, Subject> getSubjects() {
        return subjects;
    }

    public void updateSubject(Subject subject) {
        subjects.put(subject.getSubjectCode(), subject);
    }

    public Subject getSubject(String subjectCode) {
        // Return the correct Subject object based on the provided subjectCode
        return subjects.get(subjectCode);
    }
}


