package edu.pw.apsienrollment.authentication;

import edu.pw.apsienrollment.user.db.User;

public interface AuthenticationService {
    User authenticate(String username, String password);

    User authenticateFromRefreshToken(String refreshToken);

    void invalidateRefreshToken(String token);

    String getAccessToken(User user);

    String getRefreshToken(User user);

    User getAuthenticatedUser();
}
