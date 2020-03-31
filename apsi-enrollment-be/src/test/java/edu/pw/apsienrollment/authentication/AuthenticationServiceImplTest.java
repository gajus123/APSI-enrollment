package edu.pw.apsienrollment.authentication;

import edu.pw.apsienrollment.authentication.exception.InvalidCredentialsException;
import edu.pw.apsienrollment.authentication.exception.RefreshTokenExpiredException;
import edu.pw.apsienrollment.user.UserNotFoundException;
import edu.pw.apsienrollment.user.UserService;
import edu.pw.apsienrollment.user.db.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

class AuthenticationServiceImplTest {
    private static String SECRET_KEY = "secret_key";

    private UserService userService;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        authenticationService = new AuthenticationServiceImpl(userService, SECRET_KEY);
    }

    @Test
    void authenticate_success() {
        final String username = "username";
        final String password = "password";

        User user = Mockito.mock(User.class);

        Mockito.when(user.getUsername()).thenReturn(username);
        Mockito.when(user.getPassword()).thenReturn("$2y$10$5n1s6V3f5WqiXfgQzPTPjeUc5ARn.Al9hZkaJPiX0P0N5MkS6C9xO");
        Mockito.when(userService.getUserByUsername(username)).thenReturn(user);

        Assertions.assertEquals(username, authenticationService.authenticate(username, password));
    }

    @Test()
    void authenticate_incorrectCredentials() {
        final String username = "username";
        final String password = "password";

        User user = Mockito.mock(User.class);

        Mockito.when(user.getPassword()).thenReturn("$2y$10$5n1s6V3f5WqiXfgQzPTPjeUc5ARn.Al9hZkbJPiX0P0N5MkS6C9xO");
        Mockito.when(userService.getUserByUsername(username)).thenReturn(user);

        Assertions.assertThrows(InvalidCredentialsException.class,
                () -> authenticationService.authenticate(username, password));
    }

    @Test
    void authenticate_userNotFound() {
        final String username = "username";
        final String password = "password";

        Mockito.when(userService.getUserByUsername(username)).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> authenticationService.authenticate(username, password));
    }

    @Test
    void authenticateFromRefreshToken_success() {
        String username = "username";
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(AuthTokenType.REFRESH_TOKEN.getValidityPeriod());
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();

        Assertions.assertEquals(username, authenticationService.authenticateFromRefreshToken(token));
    }

    @Test
    void authenticateFromRefreshToken_tokenExpired() {
        String username = "username";
        LocalDateTime expiresAt = LocalDateTime.now()
                .minus(AuthTokenType.REFRESH_TOKEN.getValidityPeriod())
                .minusDays(1);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();

        Assertions.assertThrows(RefreshTokenExpiredException.class,
                () -> authenticationService.authenticateFromRefreshToken(token));
    }
}