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

    public HashMap<String, Subject> readCsvData(String csvPath) throws IOException {
        HashMap<String, Subject> subjects = new HashMap<>();
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

                if (data.length < 8) {
                    csvReader.close();
                    throw new IOException("Formato no válido en el archivo CSV");
                }

                Subject subject = new Subject(subjectCode, subjectName, subjectTheoricalDays, subjectPracticalDays, subjectTimeStart, subjectTimeEnd, startDate, endDate);
                subject.setTheoreticalAttendanceData(theoreticalAttendanceData);
                subject.setPracticalAttendanceData(practicalAttendanceData);
                subjects.put(subjectCode, subject);
            }
            csvReader.close();
        } else {
            throw new IOException("No se encontró el archivo CSV");
        }
        return subjects;
    }
}
