package edu.pw.apsienrollment.user;

public class UserNotFoundException extends RuntimeException {
    UserNotFoundException() {
        super("User not found");
    }
}
