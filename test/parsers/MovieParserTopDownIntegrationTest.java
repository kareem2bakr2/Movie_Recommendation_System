package parsers;
import models.Movie;
import validators.MovieValidator;
import exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieParserTopDownIntegrationTest {

    @Mock
    private MovieValidator mockValidator;

    private MovieParser movieParser;

    @TempDir
    Path tempDir;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        movieParser = new MovieParser();
        try {
            Field validatorField =
                    MovieParser.class.getDeclaredField("validator");
            validatorField.setAccessible(true);
            validatorField.set(movieParser, mockValidator);
        } catch (Exception e) {
            fail("Failed to inject mock validator: " + e.getMessage());
        }
    }



    @Test
    void testParseMovies_ValidSingleMovie() throws Exception {

        String filename = createTempFile(
                "The Matrix, TM991\n" +
                        "Action, Sci-Fi, Thriller"
        );

        doNothing().when(mockValidator).validateTitle("The Matrix");
        doNothing().when(mockValidator).validateMovieId("The Matrix", "TM991");


        List<Movie> movies = movieParser.parseMovies(filename);


        assertEquals(1, movies.size());
        Movie movie = movies.get(0);
        assertEquals("The Matrix", movie.getTitle());
        assertEquals("TM991", movie.getId());
        assertEquals(3, movie.getGenres().size());
        assertTrue(movie.getGenres().contains("Action"));
        assertTrue(movie.getGenres().contains("Sci-Fi"));
        assertTrue(movie.getGenres().contains("Thriller"));

        verify(mockValidator).validateTitle("The Matrix");
        verify(mockValidator).validateMovieId("The Matrix", "TM991");
    }

    @Test
    void testParseMovies_MultipleMovies() throws Exception {
        // Arrange
        String filename = createTempFile(
                "The Matrix, TM111\n" +
                        "Action, Sci-Fi\n" +
                        "Inception, I232\n" +
                        "Action, Thriller"
        );

        doNothing().when(mockValidator).validateTitle(anyString());
        doNothing().when(mockValidator).validateMovieId(anyString(), anyString());

        // Act
        List<Movie> movies = movieParser.parseMovies(filename);

        // Assert
        assertEquals(2, movies.size());
        assertEquals("The Matrix", movies.get(0).getTitle());
        assertEquals("TM111", movies.get(0).getId());
        assertEquals("Inception", movies.get(1).getTitle());
        assertEquals("I232", movies.get(1).getId());

        verify(mockValidator, times(2)).validateTitle(anyString());
        verify(mockValidator, times(2)).validateMovieId(anyString(), anyString());
    }

    @Test
    void testParseMovies_TitleWithComma() throws Exception {
        // Arrange
        String filename = createTempFile(
                "Interstellar, I433\n" +
                        "Western, Action"
        );

        doNothing().when(mockValidator).validateTitle(anyString());
        doNothing().when(mockValidator).validateMovieId(anyString(), anyString());

        List<Movie> movies = movieParser.parseMovies(filename);

        assertEquals(1, movies.size());
        assertEquals("Interstellar", movies.get(0).getTitle());
        assertEquals("I433", movies.get(0).getId());
    }

    @Test
    void testParseMovies_SingleGenre() throws Exception {
        // Arrange
        String filename = createTempFile(
                "Simple Movie, SM344\n" +
                        "Drama"
        );

        doNothing().when(mockValidator).validateTitle(anyString());
        doNothing().when(mockValidator).validateMovieId(anyString(), anyString());

        List<Movie> movies = movieParser.parseMovies(filename);

        assertEquals(1, movies.size());
        assertEquals(1, movies.get(0).getGenres().size());
        assertTrue(movies.get(0).getGenres().contains("Drama"));
    }

    @Test
    void testParseMovies_WhitespaceHandling() throws Exception {
        // Arrange
        String filename = createTempFile(
                "  Spaced Title  ,   ST235  \n" +
                        "  Action  ,  Comedy  "
        );

        doNothing().when(mockValidator).validateTitle(anyString());
        doNothing().when(mockValidator).validateMovieId(anyString(), anyString());

        // Act
        List<Movie> movies = movieParser.parseMovies(filename);

        // Assert
        assertEquals(1, movies.size());
        assertEquals("Spaced Title", movies.get(0).getTitle());
        assertEquals("ST235", movies.get(0).getId());
        assertTrue(movies.get(0).getGenres().contains("Action"));
        assertTrue(movies.get(0).getGenres().contains("Comedy"));
    }

    @Test
    void testParseMovies_InvalidFormat_NoComma() throws Exception {
        String filename = createTempFile("Invalid Movie Without Comma");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> movieParser.parseMovies(filename)
        );

        assertEquals("Invalid movie format", exception.getMessage());
        verify(mockValidator, never()).validateTitle(anyString());
    }

    @Test
    void testParseMovies_MissingGenresLine() throws Exception {
        String filename = createTempFile("Movie Title, MV006");

        doNothing().when(mockValidator).validateTitle(anyString());
        doNothing().when(mockValidator).validateMovieId(anyString(), anyString());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> movieParser.parseMovies(filename)
        );

        assertEquals("Missing genres line", exception.getMessage());
    }

    @Test
    void testParseMovies_FileNotFound() {
        assertThrows(
                IOException.class,
                () -> movieParser.parseMovies("nonexistent_file.txt")
        );
    }

    @Test
    void testParseMovies_EmptyFile() throws Exception {
        String filename = createTempFile("");

        List<Movie> movies = movieParser.parseMovies(filename);

        assertTrue(movies.isEmpty());
        verify(mockValidator, never()).validateTitle(anyString());
    }

    private String createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test_movies.txt");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(content);
        }
        return file.toString();
    }
}