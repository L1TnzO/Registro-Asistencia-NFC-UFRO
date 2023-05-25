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
                String subjectTheoricalDays = data[4];
                String subjectPracticalDays = data[5];
                LocalDate startDate = LocalDate.parse(data[6]);
                LocalDate endDate = LocalDate.parse(data[7]);

                LinkedHashMap<LocalDate, Boolean> theoreticalAttendanceData = new LinkedHashMap<>();
                LinkedHashMap<LocalDate, Boolean> practicalAttendanceData = new LinkedHashMap<>();
                if (data.length > 8) {
                    for (int i = 8; i < data.length; i++) {
                        if (!data[i].isEmpty()) {  // Check if the part is not empty
                            String[] parts = data[i].split(":");
                            LocalDate date = LocalDate.parse(parts[0]);
                            Boolean attendance = Boolean.parseBoolean(parts[1]);
                            if (subjectTheoricalDays.charAt(date.getDayOfWeek().getValue() - 1) == '1') {
                                theoreticalAttendanceData.put(date, attendance);
                            } else if (subjectPracticalDays.charAt(date.getDayOfWeek().getValue() - 1) == '1') {
                                practicalAttendanceData.put(date, attendance);
                            }
                        }
                    }
                }


                Subject subject = new Subject(subjectCode, subjectName, subjectTheoricalDays, subjectPracticalDays, subjectTimeStart, subjectTimeEnd, startDate, endDate);
                subject.setTheoreticalAttendanceData(theoreticalAttendanceData);
                subject.setPracticalAttendanceData(practicalAttendanceData);
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
            row.add(subject.getSubjectTheoricalDays());
            row.add(subject.getSubjectPracticalDays());
            row.add(subject.getStartDate().toString());
            row.add(subject.getEndDate().toString());

            StringBuilder theoreticalAttendanceData = new StringBuilder();
            StringBuilder practicalAttendanceData = new StringBuilder();
            for (Map.Entry<LocalDate, Boolean> entry : subject.getTheoreticalAttendanceData().entrySet()) {
                theoreticalAttendanceData.append(entry.getKey().toString())
                        .append(":")
                        .append(entry.getValue().toString())
                        .append(",");
            }
            for (Map.Entry<LocalDate, Boolean> entry : subject.getPracticalAttendanceData().entrySet()) {
                practicalAttendanceData.append(entry.getKey().toString())
                        .append(":")
                        .append(entry.getValue().toString())
                        .append(",");
            }

            // Include both theoretical and practical attendance data
            row.add(theoreticalAttendanceData.toString());
            row.add(practicalAttendanceData.toString());
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


}


