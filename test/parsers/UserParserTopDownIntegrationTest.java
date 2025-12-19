package parsers;
import models.User;
import validators.UserValidator;
import exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserParserTopDownIntegrationTest {

    @Mock
    private UserValidator mockValidator;

    private UserParser userParser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userParser = new UserParser();

        try {
            java.lang.reflect.Field validatorField =
                    UserParser.class.getDeclaredField("validator");
            validatorField.setAccessible(true);
            validatorField.set(userParser, mockValidator);

            doNothing().when(mockValidator).validateUserName(anyString());
            doNothing().when(mockValidator).validateUserId(anyString());
            doNothing().when(mockValidator).validateUniqueUserId(anyString(), any(Set.class));
        } catch (Exception e) {
            fail("Failed to inject mock validator: " + e.getMessage());
        }
    }

    @Test
    void testParseUsers_ValidSingleUser() throws Exception {
        String filename = createTempFile(
                "John Smith,123456789\n" +
                        "TDK123,I456"
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("John Smith", user.getName());
        assertEquals("123456789", user.getId());
        assertEquals(2, user.getLikedMovieIds().size());
        assertTrue(user.getLikedMovieIds().contains("TDK123"));
        assertTrue(user.getLikedMovieIds().contains("I456"));
    }

    @Test
    void testParseUsers_ValidMultipleUsers() throws Exception {
        String filename = createTempFile(
                "John Smith,123456789\n" +
                        "TDK123,I456\n" +
                        "Alice Johnson,987654321\n" +
                        "TSR789\n" +
                        "Bob Williams,123456780\n" +
                        "FN012,I678"
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(3, users.size());

        assertEquals("John Smith", users.get(0).getName());
        assertEquals("123456789", users.get(0).getId());
        assertEquals(2, users.get(0).getLikedMovieIds().size());

        assertEquals("Alice Johnson", users.get(1).getName());
        assertEquals("987654321", users.get(1).getId());
        assertEquals(1, users.get(1).getLikedMovieIds().size());

        assertEquals("Bob Williams", users.get(2).getName());
        assertEquals("123456780", users.get(2).getId());
        assertEquals(2, users.get(2).getLikedMovieIds().size());
    }

    @Test
    void testParseUsers_SingleLikedMovie() throws Exception {
        String filename = createTempFile(
                "Jane Doe,555666777\n" +
                        "MOV123"
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getLikedMovieIds().size());
        assertTrue(users.get(0).getLikedMovieIds().contains("MOV123"));
    }

    @Test
    void testParseUsers_WhitespaceIsTrimmedInOutput() throws Exception {
        String filename = createTempFile(
                "  John Doe  ,  999888777  \n" +
                        "  MOV1  ,  MOV2  ,  MOV3  "
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("999888777", users.get(0).getId());
        assertTrue(users.get(0).getLikedMovieIds().contains("MOV1"));
        assertTrue(users.get(0).getLikedMovieIds().contains("MOV2"));
        assertTrue(users.get(0).getLikedMovieIds().contains("MOV3"));
    }

    @Test
    void testParseUsers_InvalidFormat_NoComma() throws Exception {
        String filename = createTempFile("Invalid User Without Comma");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userParser.parseUsers(filename)
        );

        assertEquals("Invalid user format", exception.getMessage());
    }

    @Test
    void testParseUsers_MissingLikedMoviesLine() throws Exception {
        String filename = createTempFile("John Smith,123456789");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userParser.parseUsers(filename)
        );

        assertEquals("Missing liked movies line", exception.getMessage());
    }

    @Test
    void testParseUsers_MissingLikedMoviesLine_MultipleUsers() throws Exception {
        String filename = createTempFile(
                "John Smith,123456789\n" +
                        "MOV123\n" +
                        "Jane Doe,987654321"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userParser.parseUsers(filename)
        );

        assertEquals("Missing liked movies line", exception.getMessage());
    }

    @Test
   void testParseUsers_FileNotFound() {
        assertThrows(
                IOException.class,
                () -> userParser.parseUsers("nonexistent_file.txt")
        );
    }

    @Test
    void testParseUsers_EmptyFile() throws Exception {
        String filename = createTempFile("");

        List<User> users = userParser.parseUsers(filename);

        assertTrue(users.isEmpty());
    }

    @Test
    void testParseUsers_ManyLikedMovies() throws Exception {
        String filename = createTempFile(
                "Movie Fan,444555666\n" +
                        "M441,M542,M643,M774,M545,M654,M754,M812,M933,M150"
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(1, users.size());
        assertEquals(10, users.get(0).getLikedMovieIds().size());
        assertTrue(users.get(0).getLikedMovieIds().contains("M441"));
        assertTrue(users.get(0).getLikedMovieIds().contains("M774"));
    }

    @Test
    void testParseUsers_ValidatorReceivesUntrimmedValues() throws Exception {
        String filename = createTempFile(
                "John Smith,123456789\n" +
                        "MOV123"
        );

        userParser.parseUsers(filename);

        verify(mockValidator).validateUserName("John Smith");
        verify(mockValidator).validateUserId("123456789");
        verify(mockValidator).validateUniqueUserId(eq("123456789"), any(Set.class));
    }

    @Test
    void testParseUsers_ComplexRealWorldData() throws Exception {
        String filename = createTempFile(
                "John Smith,123456789\n" +
                        "TDK123,I456\n" +
                        "Alice Johnson,987654321\n" +
                        "TSR789\n" +
                        "Bob Williams,123456780\n" +
                        "FN012,I678"
        );

        List<User> users = userParser.parseUsers(filename);

        assertEquals(3, users.size());

        assertEquals("John Smith", users.get(0).getName());
        assertEquals("123456789", users.get(0).getId());
        assertEquals(2, users.get(0).getLikedMovieIds().size());
        assertTrue(users.get(0).getLikedMovieIds().contains("TDK123"));
        assertTrue(users.get(0).getLikedMovieIds().contains("I456"));

        assertEquals("Alice Johnson", users.get(1).getName());
        assertEquals("987654321", users.get(1).getId());
        assertEquals(1, users.get(1).getLikedMovieIds().size());
        assertTrue(users.get(1).getLikedMovieIds().contains("TSR789"));

        // User 3
        assertEquals("Bob Williams", users.get(2).getName());
        assertEquals("123456780", users.get(2).getId());
        assertEquals(2, users.get(2).getLikedMovieIds().size());
        assertTrue(users.get(2).getLikedMovieIds().contains("FN012"));
        assertTrue(users.get(2).getLikedMovieIds().contains("I678"));
    }

    private String createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test_users.txt");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(content);
        }
        return file.toString();
    }
}