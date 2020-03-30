package edu.pw.apsienrollment.user.api.dto;

import edu.pw.apsienrollment.user.db.User;
import edu.pw.apsienrollment.user.db.UserRole;
import lombok.Value;

import java.util.Set;

@Value
public class UserDto {
    Integer id;
    String username;
    String name;
    String surname;
    String email;
    Set<UserRole> userRoles;

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getName(), user.getSurname(), user.getEmail(), user.getRoles());
    }
}
