package parsers;

import models.Movie;
import validators.MovieValidator;
import exceptions.ValidationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MovieParser {
    private MovieValidator validator;

    public MovieParser() {
        this.validator = new MovieValidator();
    }

    public List<Movie> parseMovies(String filename)
            throws ValidationException, IOException {
        List<Movie> movies = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] titleIdParts = line.split(",", 2);
            if (titleIdParts.length != 2) {
                reader.close();
                throw new ValidationException("Invalid movie format");
            }

            String title = titleIdParts[0].trim();
            String id = titleIdParts[1].trim();

            validator.validateTitle(title);
            validator.validateMovieId(title, id);

            line = reader.readLine();
            if (line == null) {
                reader.close();
                throw new ValidationException("Missing genres line");
            }

            String[] genreArray = line.split(",");
            List<String> genres = new ArrayList<>();
            for (String genre : genreArray) {
                genres.add(genre.trim());
            }

            movies.add(new Movie(title, id, genres));
        }

        reader.close();
        return movies;
    }
}