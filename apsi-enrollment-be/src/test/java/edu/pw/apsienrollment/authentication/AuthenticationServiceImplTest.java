package edu.pw.apsienrollment.authentication;

import edu.pw.apsienrollment.authentication.db.RefreshToken;
import edu.pw.apsienrollment.authentication.db.RefreshTokenRepository;
import edu.pw.apsienrollment.authentication.exception.InvalidCredentialsException;
import edu.pw.apsienrollment.authentication.exception.RefreshTokenIsNotValidException;
import edu.pw.apsienrollment.authentication.exception.RefreshTokenNotFoundException;
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
import java.util.Optional;

class AuthenticationServiceImplTest {
    private static String SECRET_KEY = "secret_key";

    private UserService userService;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        authenticationService = new AuthenticationServiceImpl(refreshTokenRepository, userService, SECRET_KEY);
    }

    @Test
    void authenticate_success() {
        final String username = "username";
        final String password = "password";

        User user = Mockito.mock(User.class);

        Mockito.when(user.getUsername()).thenReturn(username);
        Mockito.when(user.getPassword()).thenReturn("$2y$10$5n1s6V3f5WqiXfgQzPTPjeUc5ARn.Al9hZkaJPiX0P0N5MkS6C9xO");
        Mockito.when(userService.getUserByUsername(username)).thenReturn(user);

        Assertions.assertEquals(user, authenticationService.authenticate(username, password));
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
        User user = Mockito.mock(User.class);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(AuthTokenType.REFRESH_TOKEN.getValidityPeriod());
        String token = "qwertyuiop";

        RefreshToken refreshToken = Mockito.mock(RefreshToken.class);
        Mockito.when(refreshToken.getExpiresAt()).thenReturn(expiresAt);
        Mockito.when(refreshToken.isValid()).thenReturn(true);
        Mockito.when(refreshToken.getUser()).thenReturn(user);
        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        Assertions.assertEquals(user, authenticationService.authenticateFromRefreshToken(token));
    }

    @Test
    void authenticateFromRefreshToken_tokenExpired() {
        LocalDateTime expiresAt = LocalDateTime.now()
                .minus(AuthTokenType.REFRESH_TOKEN.getValidityPeriod())
                .minusDays(1);
        String token = "qwertyuiop";

        RefreshToken refreshToken = Mockito.mock(RefreshToken.class);
        Mockito.when(refreshToken.getExpiresAt()).thenReturn(expiresAt);
        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        Assertions.assertThrows(RefreshTokenIsNotValidException.class,
                () -> authenticationService.authenticateFromRefreshToken(token));
    }

    @Test
    void authenticateFromRefreshToken_tokenInvalid() {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(AuthTokenType.REFRESH_TOKEN.getValidityPeriod());
        String token = "qwertyuiop";

        RefreshToken refreshToken = Mockito.mock(RefreshToken.class);
        Mockito.when(refreshToken.getExpiresAt()).thenReturn(expiresAt);
        Mockito.when(refreshToken.isValid()).thenReturn(false);
        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        Assertions.assertThrows(RefreshTokenIsNotValidException.class,
                () -> authenticationService.authenticateFromRefreshToken(token));
    }

    @Test
    void authenticateFromRefreshToken_notFound() {
        String token = "qwertyuiop";

        Mockito.when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        Assertions.assertThrows(RefreshTokenNotFoundException.class,
                () -> authenticationService.authenticateFromRefreshToken(token));
    }


}