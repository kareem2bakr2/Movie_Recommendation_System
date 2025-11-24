package parsers;

import models.User;
import validators.UserValidator;
import exceptions.ValidationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class UserParser {
    private UserValidator validator;

    public UserParser() {
        this.validator = new UserValidator();
    }

    public List<User> parseUsers(String filename)
            throws ValidationException, IOException {
        List<User> users = new ArrayList<>();
        Set<String> userIds = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] nameIdParts = line.split(",", 2);
            if (nameIdParts.length != 2) {
                reader.close();
                throw new ValidationException("Invalid user format");
            }

            String name = nameIdParts[0].trim();
            String id = nameIdParts[1].trim();

            validator.validateUserName(name);
            validator.validateUserId(id);
            validator.validateUniqueUserId(id, userIds);
            userIds.add(id);

            line = reader.readLine();
            if (line == null) {
                reader.close();
                throw new ValidationException("Missing liked movies line");
            }

            String[] movieIdArray = line.split(",");
            List<String> likedMovieIds = new ArrayList<>();
            for (String movieId : movieIdArray) {
                likedMovieIds.add(movieId.trim());
            }

            users.add(new User(name, id, likedMovieIds));
        }

        reader.close();
        return users;
    }
}