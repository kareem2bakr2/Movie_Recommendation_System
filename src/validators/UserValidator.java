package validators;

import exceptions.UserNameException;
import exceptions.UserIdException;
import java.util.Set;

public class UserValidator {

    public void validateUserName(String name) throws UserNameException {
        if (name == null || name.isEmpty() || name.startsWith(" ")) {
            throw new UserNameException(name);
        }

        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                throw new UserNameException(name);
            }
        }
    }

    public void validateUserId(String userId) throws UserIdException {
        if (userId == null || userId.length() != 9) {
            throw new UserIdException(userId);
        }

        if (!Character.isDigit(userId.charAt(0))) {
            throw new UserIdException(userId);
        }

        char lastChar = userId.charAt(8);
        boolean lastIsLetter = Character.isLetter(lastChar);

        int digitCount = lastIsLetter ? 8 : 9;

        for (int i = 0; i < digitCount; i++) {
            if (!Character.isDigit(userId.charAt(i))) {
                throw new UserIdException(userId);
            }
        }

        if (lastIsLetter) {
            for (int i = 0; i < 8; i++) {
                if (Character.isLetter(userId.charAt(i))) {
                    throw new UserIdException(userId);
                }
            }
        }
    }

    public void validateUniqueUserId(String userId, Set<String> existingIds)
            throws UserIdException {
        if (existingIds.contains(userId)) {
            throw new UserIdException(userId);
        }
    }
}