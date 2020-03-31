package edu.pw.apsienrollment.authentication.api.dto;

import edu.pw.apsienrollment.user.db.User;
import edu.pw.apsienrollment.user.db.UserRole;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class AuthenticatedUserDto {
    Integer id;
    String username;
    String name;
    String surname;
    String email;
    Set<UserRole> userRoles;

    public static AuthenticatedUserDto of(User user) {
        return AuthenticatedUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .userRoles(user.getRoles())
                .build();
    }
}
