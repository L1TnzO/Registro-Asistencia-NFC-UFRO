package com.yourdomain.AsistenciaNFC;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.UFRO.AsistenciaNFC.data.Subject;
import com.UFRO.AsistenciaNFC.data.SubjectManager;

@RunWith(MockitoJUnitRunner.class)
public class AttendanceManagerTest {

    private AttendanceManager attendanceManager;

    @Mock
    private SubjectManager subjectManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        attendanceManager = new AttendanceManager(subjectManager);
    }

    @Test
    public void testUpdateAttendance() throws IOException {
        String nfcCode = "8276295736295424";
        HashMap<String, Subject> mockSubjects = new HashMap<>();
        Subject mockSubject = mock(Subject.class);

        when(mockSubject.getSubjectCode()).thenReturn(nfcCode);
        when(mockSubject.getSubjectTheoricalDays()).thenReturn("1111111");
        when(mockSubject.getSubjectPracticalDays()).thenReturn("0000000");
        when(mockSubject.getSubjectTimeStart()).thenReturn(LocalTime.of(00, 01));
        when(mockSubject.getSubjectTimeEnd()).thenReturn(LocalTime.of(23, 59));

        mockSubjects.put(nfcCode, mockSubject);
        when(subjectManager.getSubjects()).thenReturn(mockSubjects);

        attendanceManager.updateAttendance(nfcCode);

        verify(subjectManager, times(1)).saveSubjects();
    }

    @Test(expected = IOException.class)
    public void testUpdateAttendance_noSubjects() throws IOException {
        String nfcCode = "8276295736295424";
        HashMap<String, Subject> mockSubjects = new HashMap<>();
        when(subjectManager.getSubjects()).thenReturn(mockSubjects);

        attendanceManager.updateAttendance(nfcCode);
    }

    @Test(expected = IOException.class)
    public void testUpdateAttendance_wrongNfcCode() throws IOException {
        String nfcCode = "1111111111111111";
        HashMap<String, Subject> mockSubjects = new HashMap<>();
        Subject mockSubject = mock(Subject.class);

        when(mockSubject.getSubjectCode()).thenReturn("2222222222222222");
        mockSubjects.put(nfcCode, mockSubject);
        when(subjectManager.getSubjects()).thenReturn(mockSubjects);

        attendanceManager.updateAttendance(nfcCode);
    }

    @Test
    public void testUpdateAttendance_noMatchingDay() throws IOException {
        String nfcCode = "8276295736295424";
        HashMap<String, Subject> mockSubjects = new HashMap<>();
        Subject mockSubject = mock(Subject.class);

        when(mockSubject.getSubjectCode()).thenReturn(nfcCode);
        when(mockSubject.getSubjectTheoricalDays()).thenReturn("0000000");
        when(mockSubject.getSubjectPracticalDays()).thenReturn("0000000");
        when(mockSubject.getSubjectTimeStart()).thenReturn(LocalTime.of(00, 01));
        when(mockSubject.getSubjectTimeEnd()).thenReturn(LocalTime.of(23, 59));

        mockSubjects.put(nfcCode, mockSubject);
        when(subjectManager.getSubjects()).thenReturn(mockSubjects);

        attendanceManager.updateAttendance(nfcCode);

        verify(subjectManager, times(0)).saveSubjects();
    }

    @Test
    public void testUpdateAttendance_noMatchingTime() throws IOException {
        String nfcCode = "8276295736295424";
        HashMap<String, Subject> mockSubjects = new HashMap<>();
        Subject mockSubject = mock(Subject.class);

        when(mockSubject.getSubjectCode()).thenReturn(nfcCode);
        when(mockSubject.getSubjectTheoricalDays()).thenReturn("1111111");
        when(mockSubject.getSubjectPracticalDays()).thenReturn("0000000");
        when(mockSubject.getSubjectTimeStart()).thenReturn(LocalTime.of(00, 01));
        when(mockSubject.getSubjectTimeEnd()).thenReturn(LocalTime.of(01, 01));

        mockSubjects.put(nfcCode, mockSubject);
        when(subjectManager.getSubjects()).thenReturn(mockSubjects);

        attendanceManager.updateAttendance(nfcCode);

        verify(subjectManager, times(0)).saveSubjects();
    }
}


