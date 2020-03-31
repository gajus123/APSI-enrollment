package edu.pw.apsienrollment.authentication.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException() {
        super("Refresh token expired. Authenticate again");
    }
}
