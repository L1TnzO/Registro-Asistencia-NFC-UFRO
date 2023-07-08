package com.UFRO.AsistenciaNFC.data;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvWriter {

    public void writeSubjects(String csvFilePath, Map<String, Subject> subjects, AttendanceManager attendanceManager) throws IOException {
        FileWriter csvWriter = new FileWriter(csvFilePath);
        csvWriter.append("Nombre Asignatura,ID NFC,Hora comienzo,Hora fin,Dias semana teoricos, Dias semana practicos,Comienzo semestre,Fin semestre,Dias clases\n");
        for (Subject subject : subjects.values()) {
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
}

