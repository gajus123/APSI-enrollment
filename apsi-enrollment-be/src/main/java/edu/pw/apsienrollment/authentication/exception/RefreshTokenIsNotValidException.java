package edu.pw.apsienrollment.authentication.exception;

public class RefreshTokenIsNotValidException extends RuntimeException {
    public RefreshTokenIsNotValidException() {
        super("Refresh token expired or invalidated. Authenticate again");
    }
}
