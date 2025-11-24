package services;

import models.UserRecommendation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RecommendationWriter {

    public void writeRecommendations(String filename, List<UserRecommendation> recommendations)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        for (UserRecommendation rec : recommendations) {
            writer.write(rec.getUserName() + "," + rec.getUserId());
            writer.newLine();

            writer.write(String.join(",", rec.getRecommendedMovieTitles()));
            writer.newLine();
        }

        writer.close();
    }

    public void writeError(String filename, String errorMessage) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(errorMessage);
        writer.close();
    }
}