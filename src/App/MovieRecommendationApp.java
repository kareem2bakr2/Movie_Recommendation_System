package App;

import models.Movie;
import models.User;
import models.UserRecommendation;
import parsers.MovieParser;
import parsers.UserParser;
import services.RecommendationEngine;
import services.RecommendationWriter;
import exceptions.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class MovieRecommendationApp {

    public static void main(String[] args) {
    	
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the movies file path: ");
        String moviesFile = scanner.nextLine().trim();
                                                           // edit to make user enter the name of wanted in execution time 
        System.out.print("Enter the users file path: "); 
        String usersFile = scanner.nextLine().trim();
        scanner.close();
        
        String outputFile = "recommendations.txt";

        RecommendationWriter writer = new RecommendationWriter();

        try {
            MovieParser movieParser = new MovieParser();
            List<Movie> movies = movieParser.parseMovies(moviesFile);

            UserParser userParser = new UserParser();
            List<User> users = userParser.parseUsers(usersFile);

            RecommendationEngine engine = new RecommendationEngine(movies);
            List<UserRecommendation> recommendations = new ArrayList<>();

            for (User user : users) {
                UserRecommendation rec = engine.generateRecommendations(user);
                recommendations.add(rec);
            }

            writer.writeRecommendations(outputFile, recommendations);

            System.out.println("Recommendations generated successfully!");

        } catch (ValidationException e) {
            try {
                writer.writeError(outputFile, e.getMessage());
                System.out.println("Validation error: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Failed to write error: " + ioException.getMessage());
            }
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        }
    }
}