package AppTest;

import App.MovieRecommendationApp;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MovieRecommendationAppTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreSystemIO() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testMainSuccessFlow() throws Exception {

        // Input files must exist
        String input =
                "movies.txt\n" +
                        "users.txt\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        MovieRecommendationApp.main(new String[]{});

        String output = baos.toString();
        assertTrue(output.contains("Recommendations generated successfully!"));

        String fileContent =
                Files.readString(Path.of("recommendations.txt"));
        assertFalse(fileContent.isBlank());
    }


    @Test
    void testMainValidationError() throws Exception {

        Files.writeString(Path.of("movies_invalid.txt"),
                "Inception M1\nSci-Fi\n");

        Files.writeString(Path.of("users_test.txt"),
                "Kareem,U1\nM1\n");

        String input =
                "movies_invalid.txt\n" +
                        "users_test.txt\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        MovieRecommendationApp.main(new String[]{});

        String output = baos.toString();
        assertTrue(output.contains("Validation error"));

        String fileContent =
                Files.readString(Path.of("recommendations.txt"));
        assertFalse(fileContent.isBlank());
    }


}
