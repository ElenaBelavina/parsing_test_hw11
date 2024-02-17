import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import qa.guru.model.Car;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class FileParsingTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassLoader cl = FileParsingTest.class.getClassLoader();


    @DisplayName("Парсинг JSON-файла")
    @Test
    void jsonParsingTestNextLevel() throws Exception {
        try (InputStream is = cl.getResourceAsStream("car.json");
             Reader reader = new InputStreamReader(is)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Car car = objectMapper.readValue(reader, Car.class);

            Assertions.assertEquals("Audi", car.getMark());
            Assertions.assertEquals("Q8", car.getModel());
            Assertions.assertEquals("blue", car.getColor());
            Assertions.assertEquals(226, car.getMaxSpeed());
            Assertions.assertArrayEquals(
                    new String[]{"Audi virtual cockpit Plus", "HomeLink system", "Seat and mirror position memory"},
                    car.getOptions().toArray());
            Assertions.assertEquals("X007XP77", car.getSts().getNumber());
            Assertions.assertEquals("X7LLSRB1HAH550719", car.getSts().getVin());
            Assertions.assertEquals(2021, car.getSts().getYear());
        }
    }

    @DisplayName("Парсинг cvs-файла из zip-архива")
    @Test
    void cvsParsingFromZipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("1.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {

                if (entry.getName().equals("tools.cvs")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));
                    {
                        List<String[]> content = csvReader.readAll();
                        Assertions.assertArrayEquals(
                                new String[]{"Instrument", "Count", "Price", "Sum"}, content.get(0)
                        );
                    }
                    System.out.println(entry.getName());
                }

            }
        }
    }

    @DisplayName("Парсинг xls-файла из zip-архива")
    @Test
    void xlsParsingFromZipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("1.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("teachers.xls")) {
                    XLS xls = new XLS(zis);
                    Assertions.assertEquals(
                            "Информационные системы маркетинга ",
                            xls.excel.getSheet("Распределение времени")
                                    .getRow(12)
                                    .getCell(1)
                                    .getStringCellValue()
                    );
                    System.out.println(entry.getName());
                }

            }
        }
    }

    @DisplayName("Парсинг pdf-файла из zip-архива")
    @Test
    void pdfParsingFromZipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("1.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("Miele.pdf")) {
                    PDF pdf = new PDF(zis);
                    assertThat(pdf.title).isEqualTo("WCG 360_IT-it");
                    System.out.println(entry.getName());
                }
             }
        }
    }
}

