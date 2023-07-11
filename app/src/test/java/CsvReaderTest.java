import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import com.UFRO.AsistenciaNFC.data.Subject;
import com.UFRO.AsistenciaNFC.data.CsvReader;

public class CsvReaderTest {

    private CsvReader csvReader;
    private String csvPath;

    @Before
    public void setUp() {
        csvReader = new CsvReader();
        csvPath = "src/test/java/resources/test.csv";
    }

    @Test
    public void testReadCsvData() {
        HashMap<String, Subject> subjects = csvReader.readCsvData(csvPath);

        // Verifica que el mapa no esté vacío
        assertFalse(subjects.isEmpty());

        // Verifica que el mapa contenga las claves correctas
        assertTrue(subjects.containsKey("8276295736295424"));
        assertTrue(subjects.containsKey("8278494759550976"));

        // Verifica los datos de una asignatura específica
        Subject testSubject = subjects.get("8276295736295424");
        assertEquals("Test", testSubject.getSubjectName());

        Subject pooSubject = subjects.get("8278494759550976");
        assertEquals("Programacion Orientada a objetos", pooSubject.getSubjectName());
    }

    @Test
    public void testReadCsvDataWithNonexistentFile() {
        HashMap<String, Subject> subjects = csvReader.readCsvData("ruta/no/existente.csv");
        assertTrue("Expected no subjects to be read from a nonexistent CSV file", subjects.isEmpty());
    }

    @Test
    public void testReadCsvDataWithIncorrectlyFormattedFile() {
        HashMap<String, Subject> subjects = csvReader.readCsvData("src/test/java/resources/malformatted.csv");
        assertEquals("Expected one subject to be read from the incorrectly formatted CSV file", 1, subjects.size());
    }
}
