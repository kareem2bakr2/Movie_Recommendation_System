package App;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MovieRecommendationAppTopDownTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @TempDir
    Path tempDir;

    @AfterEach
    void restoreSystemIO() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testMainWorkflow_SuccessfulExecution() throws Exception {

        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n" +
                        "Interstellar,I678\n" +
                        "sci-fi,adventure,drama\n"
        );


        Files.writeString(usersFile,
                "Alice Johnson,987654321\n" +
                        "TG345\n" +
                        "Bob Williams,123456780\n" +
                        "I678\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Enter the movies file path:"),
                "Should prompt for movies file");
        assertTrue(output.contains("Enter the users file path:"),
                "Should prompt for users file");
        assertTrue(output.contains("Recommendations generated successfully!"),
                "Should show success message");

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile),
                "Should create recommendations.txt");
        String fileContent = Files.readString(outputFile);
        assertFalse(fileContent.isBlank(),
                "Recommendations file should not be empty");

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_ValidationException() throws Exception {

        Path moviesFile = tempDir.resolve("invalid_movies.txt");
        Path usersFile = tempDir.resolve("users.txt");

        Files.writeString(moviesFile,
                "Invalid Movie Format Without Comma\n" +
                        "action\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson,987654321\n" +
                        "M1\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Validation error"),
                "Should display validation error message");

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile),
                "Should create recommendations.txt with error");
        String fileContent = Files.readString(outputFile);
        assertTrue(fileContent.contains("Invalid movie format") || fileContent.contains("error"),
                "Error should be written to file");

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_IOException() throws Exception {
        String input = "nonexistent_movies.txt\nusers.txt\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorStream));

        MovieRecommendationApp.main(new String[]{});

        String errorOutput = errorStream.toString();
        assertTrue(errorOutput.contains("File error"),
                "Should display file error message");
    }

    @Test
    void testMainWorkflow_MultipleUsers() throws Exception {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n" +
                        "Interstellar,I678\n" +
                        "sci-fi,adventure,drama\n" +
                        "The Dark Knight,TDK123\n" +
                        "action,crime,drama\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson,987654321\n" +
                        "TG345\n" +
                        "Bob Williams,123456780\n" +
                        "I678,TDK123\n" +
                        "Charlie Brown,555666777\n" +
                        "TG345,I678\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Recommendations generated successfully!"));

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));
        String fileContent = Files.readString(outputFile);
        assertFalse(fileContent.isBlank());

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_EmptyUserList() throws Exception {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("empty_users.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n"
        );

        Files.writeString(usersFile, "");

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Recommendations generated successfully!"));

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_MissingGenresLine() throws Exception {
        Path moviesFile = tempDir.resolve("movies_missing_genres.txt");
        Path usersFile = tempDir.resolve("users.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson,987654321\n" +
                        "TG345\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Validation error") || output.contains("error"));

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_InvalidUserFormat() throws Exception {

        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("invalid_users.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson 987654321\n" +
                        "TG345\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Validation error"));

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_MissingLikedMoviesLine() throws Exception {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users_missing_movies.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson,987654321\n"
        );

        String input = moviesFile + "\n" + usersFile + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Validation error") );

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }

    @Test
    void testMainWorkflow_InputTrimming() throws Exception {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");

        Files.writeString(moviesFile,
                "The Godfather,TG345\n" +
                        "crime,drama\n"
        );

        Files.writeString(usersFile,
                "Alice Johnson,987654321\n" +
                        "TG345\n"
        );

        String input = "  " + moviesFile + "  \n  " + usersFile + "  \n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        MovieRecommendationApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Recommendations generated successfully!"));

        Path outputFile = Path.of("recommendations.txt");
        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }

}