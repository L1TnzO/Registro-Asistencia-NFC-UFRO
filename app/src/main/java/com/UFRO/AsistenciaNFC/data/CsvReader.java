package com.UFRO.AsistenciaNFC.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CsvReader {

    public HashMap<String, Subject> readCsvData(String csvPath) {
        HashMap<String, Subject> subjects = new HashMap<>();
        File csvFile = new File(csvPath);
        if (csvFile.isFile()) {
            try (BufferedReader csvReader = new BufferedReader(new FileReader(csvPath))) {
                String row;
                boolean firstRow = true;
                while ((row = csvReader.readLine()) != null) {
                    if (firstRow) {
                        firstRow = false;
                        continue;
                    }
                    String[] data = row.split(",");
                    if (data.length < 7) {
                        System.out.println("Formato no válido en la línea del archivo CSV, sal    tando a la siguiente línea...");
                        continue;
                    }
                    try {
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
                                if (!data[i].isEmpty()) {
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
                    } catch (Exception e) {
                        System.out.println("Error en el formato del archivo CSV en una línea específica: " + e.getMessage() + ". Saltando a la siguiente línea...");
                    }
                }
            } catch (IOException e) {
                System.out.println("No se pudo leer el archivo CSV: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró el archivo CSV");
        }
        return subjects;
    }
}