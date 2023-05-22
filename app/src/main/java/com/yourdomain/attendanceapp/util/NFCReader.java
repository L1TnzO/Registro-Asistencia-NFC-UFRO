package com.yourdomain.attendanceapp.util;

import android.nfc.NfcAdapter;
import android.util.Log;

import com.yourdomain.attendanceapp.data.AttendanceManager;
import com.yourdomain.attendanceapp.data.Subject;

import java.io.IOException;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NFCReader {

    public NfcAdapter nfcAdapter;
    public AttendanceManager attendanceManager;
    private final static Logger LOGGER = Logger.getLogger(NFCReader.class.getName());


    public NFCReader(NfcAdapter nfcAdapter, AttendanceManager attendanceManager) {
        LOGGER.info("NFCReader constructor called");
        this.nfcAdapter = nfcAdapter;
        this.attendanceManager = attendanceManager;
        LOGGER.info("NFCReader constructor completed");
    }

    public void onNfcTagScanned(String subjectCode) {
        LOGGER.info("onNfcTagScanned called with subjectCode: " + subjectCode);
        Subject subject = attendanceManager.getSubject(subjectCode);
        if (subject == null) {
            LOGGER.log(Level.WARNING, "Subject not recognized in the system");
            return;
        }

        LOGGER.log(Level.INFO, "Found subject with code: " + subject.getSubjectCode() + ", and days: " + subject.getSubjectDays());

        String subjectDays = subject.getSubjectDays();
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        if (!isSubjectDay(currentDay, subjectDays)) {
            LOGGER.log(Level.WARNING, "Subject out of schedule");
            return;
        }

        LocalTime subjectTimeStart = subject.getSubjectTimeStart();
        LocalTime subjectTimeEnd = subject.getSubjectTimeEnd();
        LocalTime currentTime = LocalTime.now();
        if (!isSubjectTime(currentTime, subjectTimeStart, subjectTimeEnd)) {
            LOGGER.log(Level.WARNING, "Subject out of schedule");
            return;
        }

        HashMap<LocalDate, Boolean> attendanceData = subject.getAttendanceData();
        LocalDate todayDate = LocalDate.now();
        if (attendanceData.containsKey(todayDate) && attendanceData.get(todayDate)) {
            LOGGER.log(Level.WARNING, "Attendance for this subject has already been marked for today");
            return;
        }

        attendanceData.put(todayDate, true);
        subject.setAttendanceData(attendanceData);
        LOGGER.log(Level.INFO, "Updated attendance data: " + attendanceData.toString());

        attendanceManager.updateSubject(subject);

        try {
            attendanceManager.saveSubjects();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not save attendance data", e);
            return;
        }


        LOGGER.log(Level.INFO, "Attendance recorded successfully");
        LOGGER.info("onNfcTagScanned completed");
    }

    private boolean isSubjectDay(DayOfWeek currentDay, String subjectDays) {
        LOGGER.info("Entering isSubjectDay(), currentDay: " + currentDay + ", subjectDays: " + subjectDays);
        boolean result = subjectDays.contains(currentDay.name().substring(0, 1).toUpperCase());
        LOGGER.info("Exiting isSubjectDay(), result: " + result);
        return result;
    }

    private boolean isSubjectTime(LocalTime currentTime, LocalTime subjectTimeStart, LocalTime subjectTimeEnd) {
        LOGGER.info("Entering isSubjectTime(), currentTime: " + currentTime + ", subjectTimeStart: " + subjectTimeStart + ", subjectTimeEnd: " + subjectTimeEnd);
        boolean result = currentTime.isAfter(subjectTimeStart) && currentTime.isBefore(subjectTimeEnd);
        LOGGER.info("Exiting isSubjectTime(), result: " + result);
        return result;
    }
}

