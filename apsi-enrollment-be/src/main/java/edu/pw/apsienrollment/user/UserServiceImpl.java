package edu.pw.apsienrollment.user;

import edu.pw.apsienrollment.user.db.User;
import edu.pw.apsienrollment.user.db.UserRepository;
import edu.pw.apsienrollment.user.exception.InvalidCredentialsException;
import edu.pw.apsienrollment.user.exception.UserNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final byte[] secretKey;

    public UserServiceImpl(UserRepository userRepository,
                           @Value("${apsi.security.secretKey}") String secretKey) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.secretKey = secretKey.getBytes();
    }

    @Override
    public User login(String username, String password) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        final boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        if (passwordMatches) {
            return user;
        } else {
            throw new InvalidCredentialsException();
        }
    }


    @Override
    public String getUsersToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    @Override
    public User getAuthenticatedUser() {
        final String email = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElseThrow(IllegalStateException::new);
        return userRepository.findByUsername(email).orElseThrow(IllegalStateException::new);
    }

    @Override
    public User getUser(Integer id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
