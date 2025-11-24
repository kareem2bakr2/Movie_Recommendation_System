package services;

import models.Movie;
import models.User;
import models.UserRecommendation;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class RecommendationEngine {
    private Map<String, Movie> movieById;
    private Map<String, Set<String>> genreToMovieIds;

    public RecommendationEngine(List<Movie> movies) {
        this.movieById = new HashMap<>();
        this.genreToMovieIds = new HashMap<>();

        for (Movie movie : movies) {
            movieById.put(movie.getId(), movie);

            for (String genre : movie.getGenres()) {
                genreToMovieIds
                        .computeIfAbsent(genre, k -> new HashSet<>())
                        .add(movie.getId());
            }
        }
    }

    public UserRecommendation generateRecommendations(User user) {
        Set<String> recommendedIds = new HashSet<>();
        Set<String> likedIds = new HashSet<>(user.getLikedMovieIds());

        for (String likedMovieId : user.getLikedMovieIds()) {
            Movie likedMovie = movieById.get(likedMovieId);
            if (likedMovie == null) continue;

            for (String genre : likedMovie.getGenres()) {
                Set<String> moviesInGenre = genreToMovieIds.get(genre);
                if (moviesInGenre != null) {
                    recommendedIds.addAll(moviesInGenre);
                }
            }
        }

        recommendedIds.removeAll(likedIds);

        List<String> recommendedTitles = new ArrayList<>();
        for (String movieId : recommendedIds) {
            Movie movie = movieById.get(movieId);
            if (movie != null) {
                recommendedTitles.add(movie.getTitle());
            }
        }

        return new UserRecommendation(user.getName(), user.getId(), recommendedTitles);
    }
}