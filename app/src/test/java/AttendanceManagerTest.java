import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

import com.UFRO.AsistenciaNFC.data.AttendanceManager;
import com.UFRO.AsistenciaNFC.data.CsvReader;
import com.UFRO.AsistenciaNFC.data.CsvWriter;
import com.UFRO.AsistenciaNFC.data.Subject;
import com.UFRO.AsistenciaNFC.data.SubjectManager;


import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class AttendanceManagerTest {

    private AttendanceManager attendanceManager;
    private SubjectManager subjectManager;

    @Before
    public void setUp() throws IOException {
        CsvReader csvReader = new CsvReader();
        CsvWriter csvWriter = new CsvWriter();
        String csvFilePath = "src/test/java/resources/test.csv";
        subjectManager = new SubjectManager(csvFilePath, csvReader, csvWriter);
    }

    @Test
    public void testUpdateAttendance_correctDayAndTime() throws IOException {
        attendanceManager = new AttendanceManager(subjectManager, LocalDate.of(2023, 3, 15), LocalTime.of(9, 0));
        attendanceManager.updateAttendance("8278494759550976");
        Subject subject = subjectManager.getSubjects().get("8278494759550976");
        assertTrue(subject.getTheoreticalAttendanceData().get(LocalDate.of(2023, 3, 15)));
    }

    @Test
    public void testUpdateAttendance_correctDayWrongTime() throws IOException {
        attendanceManager = new AttendanceManager(subjectManager, LocalDate.of(2023, 3, 22), LocalTime.of(23, 59));
        attendanceManager.updateAttendance("8278494759550976");
        Subject subject = subjectManager.getSubjects().get("8278494759550976");
        assertFalse(subject.getTheoreticalAttendanceData().get(LocalDate.of(2023, 3, 22)));
    }

    @Test
    public void testUpdateAttendance_wrongDayCorrectTime() throws IOException {
        attendanceManager = new AttendanceManager(subjectManager, LocalDate.of(2023, 3, 14), LocalTime.of(9, 0));
        attendanceManager.updateAttendance("8278494759550976");
        Subject subject = subjectManager.getSubjects().get("8278494759550976");
        assertFalse(subject.getTheoreticalAttendanceData().containsKey(LocalDate.of(2023, 3, 14)));
    }
}





