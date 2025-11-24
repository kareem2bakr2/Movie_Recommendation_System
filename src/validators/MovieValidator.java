package validators;

import exceptions.MovieTitleException;
import exceptions.MovieIdLettersException;
import exceptions.MovieIdNumbersException;
import java.util.Set;
import java.util.HashSet;

public class MovieValidator {

    public void validateTitle(String title) throws MovieTitleException {
        if (title == null || title.trim().isEmpty()) {
            throw new MovieTitleException(title);
        }

        String[] words = title.split("\\s+");
        for (String word : words) {
            if (word.isEmpty() || !Character.isUpperCase(word.charAt(0))) {
                throw new MovieTitleException(title);
            }
        }
    }

    public void validateMovieId(String title, String movieId)
            throws MovieIdLettersException, MovieIdNumbersException {

        String expectedLetters = extractCapitalLetters(title);

        if (!movieId.startsWith(expectedLetters)) {
            throw new MovieIdLettersException(movieId);
        }

        String numbersPart = movieId.substring(expectedLetters.length());

        if (numbersPart.length() != 3) {
            throw new MovieIdNumbersException(movieId);
        }

        for (char c : numbersPart.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new MovieIdNumbersException(movieId);
            }
        }

        if (!areDigitsUnique(numbersPart)) {
            throw new MovieIdNumbersException(movieId);
        }
    }

    private String extractCapitalLetters(String title) {
        StringBuilder capitals = new StringBuilder();
        for (char c : title.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitals.append(c);
            }
        }
        return capitals.toString();
    }

    private boolean areDigitsUnique(String numbers) {
        Set<Character> seen = new HashSet<>();
        for (char c : numbers.toCharArray()) {
            if (!seen.add(c)) {
                return false;
            }
        }
        return true;
    }
}