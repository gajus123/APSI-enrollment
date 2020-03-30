package edu.pw.apsienrollment.user.api.dto;

import edu.pw.apsienrollment.user.db.User;
import lombok.Value;

@Value
public class UserDto {
    String username;
    String name;
    String surname;
    String email;

    public static UserDto of(User user) {
        return new UserDto(user.getUsername(), user.getName(), user.getSurname(), user.getEmail());
    }
}
