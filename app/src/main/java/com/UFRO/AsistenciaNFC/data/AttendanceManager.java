package com.UFRO.AsistenciaNFC.data;

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
        saveSubjects();
    }

    private void loadCSVData(String csvPath) throws IOException {
        File csvFile = new File(csvPath);
        if (csvFile.isFile()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvPath));
            String row;
            boolean firstRow = true;
            while ((row = csvReader.readLine()) != null) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                String[] data = row.split(",");
                String subjectName = data[0];
                String subjectCode = data[1];
                LocalTime subjectTimeStart = LocalTime.parse(data[2], DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime subjectTimeEnd = LocalTime.parse(data[3], DateTimeFormatter.ofPattern("HH:mm"));
                LocalDate startDate = LocalDate.parse(data[5]);
                LocalDate endDate = LocalDate.parse(data[6]);
                String subjectDays = data[4];

                LinkedHashMap<LocalDate, Boolean> attendanceData = new LinkedHashMap<>();
                if (data.length > 7) {
                    for (int i = 7; i < data.length; i++) {
                        String[] parts = data[i].split(":");
                        LocalDate date = LocalDate.parse(parts[0]);
                        Boolean attendance = Boolean.parseBoolean(parts[1]);
                        attendanceData.put(date, attendance);
                    }
                }

                Subject subject = new Subject(subjectCode, subjectName, subjectDays, subjectTimeStart, subjectTimeEnd, startDate, endDate);
                subject.setAttendanceData(attendanceData);
                subjects.put(subjectCode, subject);
            }
            csvReader.close();
        } else {
            throw new FileNotFoundException("No se pudo guardar en la base de datos");
        }
    }

    public void saveSubjects() throws IOException {
        FileWriter csvWriter = new FileWriter(csvFilePath);
        csvWriter.append("Nombre Asignatura,ID NFC,Hora comienzo,Hora fin,Dias semana,Comienzo semestre,Fin semestre,Dias clases\n");
        for (Subject subject : subjects.values()) {
            generateClassDates(subject);  // Ensure all dates are populated before saving
            List<String> row = new ArrayList<>();
            row.add(subject.getSubjectName());
            row.add(subject.getSubjectCode());
            row.add(subject.getSubjectTimeStart().toString());
            row.add(subject.getSubjectTimeEnd().toString());
            row.add(subject.getSubjectDays());
            row.add(subject.getStartDate().toString());
            row.add(subject.getEndDate().toString());

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

            row.add(attendanceData.toString());
            csvWriter.append(String.join(",", row));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }



    public HashMap<String, Subject> getSubjects() {
        return subjects;
    }

    public void updateAttendance(String nfcCode) throws IOException {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Loop over the subjects to find the matching subject
        for (Subject subject : subjects.values()) {
            if (subject.getSubjectCode().equals(nfcCode)) {
                // Check if today is one of the subject's days
                int dayOfWeek = currentDate.getDayOfWeek().getValue();  // This returns 1 for Monday and 7 for Sunday
                if (subject.getSubjectDays().charAt(dayOfWeek - 1) == '1') {
                    // Check if current time is within the subject's start and end times
                    if (!currentTime.isBefore(subject.getSubjectTimeStart()) && !currentTime.isAfter(subject.getSubjectTimeEnd())) {
                        // Update attendance
                        LinkedHashMap<LocalDate, Boolean> updatedAttendance = new LinkedHashMap<>(subject.getAttendanceData());
                        updatedAttendance.put(currentDate, true);
                        subject.setAttendanceData(updatedAttendance);
                        saveSubjects();  // Save back to CSV file
                        break;
                    }
                }
            }
        }
    }


    public Subject getSubject(String subjectCode) {
        // Return the correct Subject object based on the provided subjectCode
        return subjects.get(subjectCode);
    }

    private void generateClassDates(Subject subject) {
        LocalDate currentDate = subject.getStartDate();
        LinkedHashMap<LocalDate, Boolean> attendanceData = new LinkedHashMap<>(subject.getAttendanceData());

        while (!currentDate.isAfter(subject.getEndDate())) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();  // This returns 1 for Monday and 7 for Sunday
            if (subject.getSubjectDays().charAt(dayOfWeek - 1) == '1') {
                Boolean existingValue = attendanceData.get(currentDate);
                if (existingValue == null) {
                    attendanceData.put(currentDate, false);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        subject.setAttendanceData(attendanceData);
    }


}


