package edu.pw.apsienrollment.authentication.security;

import edu.pw.apsienrollment.authentication.JwtUtils;
import edu.pw.apsienrollment.user.db.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String AUTHORIZATION_SCHEMA = "bearer ";

    private final byte[] secretKey;
    private final UserDetailsService userServiceImpl;

    JwtAuthenticationFilter(
            final AuthenticationManager authenticationManager, String secretKey, UserDetailsService userServiceImpl) {
        super(authenticationManager);
        this.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .ifPresent(this::handleAuthorizationHeader);
        chain.doFilter(request, response);
    }

    private void handleAuthorizationHeader(final String header) {
        if (!header.toLowerCase().startsWith(AUTHORIZATION_SCHEMA)) {
            return;
        }

        try {
            final String subject = JwtUtils.getSubject(header.split(" ")[1], secretKey);
            if (null != subject) {
                User user = (User) userServiceImpl.loadUserByUsername(subject);
                SecurityContextHolder.getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(subject, null, user.getAuthorities()));
            }
        } catch (final Exception e) {
            log.error("Invalid JWT", e);
        }
    }
}
