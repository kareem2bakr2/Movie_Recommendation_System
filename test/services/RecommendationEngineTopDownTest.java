package services;

import models.Movie;
import models.User;
import models.UserRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationEngineTopDownTest {

    @Mock
    private Movie mockMovie1;

    @Mock
    private Movie mockMovie2;

    @Mock
    private Movie mockMovie3;

    @Mock
    private Movie mockMovie4;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
         MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGenerateRecommendations_SingleLikedMovie() {
        when(mockMovie1.getId()).thenReturn("TDK283");
        when(mockMovie1.getTitle()).thenReturn("The Dark Knight");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Action", "Drama"));

        when(mockMovie2.getId()).thenReturn("I333");
        when(mockMovie2.getTitle()).thenReturn("Inception");
        when(mockMovie2.getGenres()).thenReturn(Arrays.asList("Action", "Thriller"));

        List<Movie> movies = Arrays.asList(mockMovie1, mockMovie2);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("John Smith");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(Arrays.asList("TDK283"));

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertNotNull(result);
        assertEquals("John Smith", result.getUserName());
        assertEquals("123456789", result.getUserId());
        assertEquals(1, result.getRecommendedMovieTitles().size());
        assertTrue(result.getRecommendedMovieTitles().contains("Inception"));
        assertFalse(result.getRecommendedMovieTitles().contains("The Dark Knight"));
    }

    @Test
    void testGenerateRecommendations_MultipleLikedMovies() {

        when(mockMovie1.getId()).thenReturn("TG331");
        when(mockMovie1.getTitle()).thenReturn("The Godfather");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Crime", "Drama"));

        when(mockMovie2.getId()).thenReturn("I222");
        when(mockMovie2.getTitle()).thenReturn("Interstellar");
        when(mockMovie2.getGenres()).thenReturn(Arrays.asList("Sci-Fi", "Adventure"));

        when(mockMovie3.getId()).thenReturn("TDK444");
        when(mockMovie3.getTitle()).thenReturn("The Dark Knight");
        when(mockMovie3.getGenres()).thenReturn(Arrays.asList("Crime", "Action"));

        when(mockMovie4.getId()).thenReturn("I838");
        when(mockMovie4.getTitle()).thenReturn("Inception");
        when(mockMovie4.getGenres()).thenReturn(Arrays.asList("Sci-Fi", "Thriller"));

        List<Movie> movies = Arrays.asList(mockMovie1, mockMovie2, mockMovie3, mockMovie4);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("Alice");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(Arrays.asList("TG331", "I222"));

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertEquals(2, result.getRecommendedMovieTitles().size());
        assertTrue(result.getRecommendedMovieTitles().contains("The Dark Knight"));
        assertTrue(result.getRecommendedMovieTitles().contains("Inception"));
    }

    @Test
    void testGenerateRecommendations_NoCommonGenres() {

        when(mockMovie1.getId()).thenReturn("AM222");
        when(mockMovie1.getTitle()).thenReturn("Action Movie");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Action"));

        when(mockMovie2.getId()).thenReturn("CM212");
        when(mockMovie2.getTitle()).thenReturn("Comedy Movie");
        when(mockMovie2.getGenres()).thenReturn(Arrays.asList("Comedy"));

        List<Movie> movies = Arrays.asList(mockMovie1, mockMovie2);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("Bob");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(Arrays.asList("AM222"));

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertEquals(0, result.getRecommendedMovieTitles().size());
    }

    @Test
    void testGenerateRecommendations_UserLikedAllMoviesInGenre() {

        when(mockMovie1.getId()).thenReturn("AF111");
        when(mockMovie1.getTitle()).thenReturn("Action First");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Action"));

        when(mockMovie2.getId()).thenReturn("AS222");
        when(mockMovie2.getTitle()).thenReturn("Action Seconed");
        when(mockMovie2.getGenres()).thenReturn(Arrays.asList("Action"));

        List<Movie> movies = Arrays.asList(mockMovie1, mockMovie2);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("Charlie");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(Arrays.asList("AF111", "AS222"));

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertEquals(0, result.getRecommendedMovieTitles().size());
    }

    @Test
    void testGenerateRecommendations_LikedMovieNotInDatabase() {
        when(mockMovie1.getId()).thenReturn("AMF111");
        when(mockMovie1.getTitle()).thenReturn("Action Movie First");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Action"));

        List<Movie> movies = Arrays.asList(mockMovie1);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("Dave");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(Arrays.asList("M999")); // Non-existent movie

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertNotNull(result);
        assertEquals(0, result.getRecommendedMovieTitles().size());
    }

    @Test
    void testGenerateRecommendations_EmptyLikedMovies() {

        when(mockMovie1.getId()).thenReturn("M111");
        when(mockMovie1.getTitle()).thenReturn("Movie");
        when(mockMovie1.getGenres()).thenReturn(Arrays.asList("Action"));

        List<Movie> movies = Arrays.asList(mockMovie1);
        RecommendationEngine engine = new RecommendationEngine(movies);

        when(mockUser.getName()).thenReturn("Eve");
        when(mockUser.getId()).thenReturn("123456789");
        when(mockUser.getLikedMovieIds()).thenReturn(new ArrayList<>());

        UserRecommendation result = engine.generateRecommendations(mockUser);

        assertEquals(0, result.getRecommendedMovieTitles().size());
    }
}