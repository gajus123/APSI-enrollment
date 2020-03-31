package edu.pw.apsienrollment.authentication;

import edu.pw.apsienrollment.user.db.User;

public interface AuthenticationService {
    String authenticate(String username, String password);

    String authenticateFromRefreshToken(String refreshToken);

    String getToken(String username, AuthTokenType tokenType);

    User getAuthenticatedUser();
}
