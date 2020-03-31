package edu.pw.apsienrollment.authentication;

import edu.pw.apsienrollment.authentication.exception.InvalidCredentialsException;
import edu.pw.apsienrollment.authentication.exception.RefreshTokenExpiredException;
import edu.pw.apsienrollment.user.UserService;
import edu.pw.apsienrollment.user.db.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final byte[] secretKey;

    AuthenticationServiceImpl(UserService userService,
                              @Value("${apsi.security.secretKey}") String secretKey) {
        this.userService = userService;
        this.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String authenticate(String username, String password) {
        final User user = userService.getUserByUsername(username);
        final boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        if (passwordMatches) {
            return user.getUsername();
        } else {
            throw new InvalidCredentialsException();
        }
    }

    @Override
    public String authenticateFromRefreshToken(String refreshToken) {
        try {
            String subject = JwtUtils.getSubject(refreshToken, secretKey);
            userService.getUserByUsername(subject);
            return subject;
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        }
    }

    @Override
    public String getToken(String username, AuthTokenType tokenType) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(tokenType.getValidityPeriod());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    @Override
    public User getAuthenticatedUser() {
        final String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElseThrow(IllegalStateException::new);
        return Optional.ofNullable(userService.getUserByUsername(username))
                .orElseThrow(IllegalStateException::new);
    }

}
