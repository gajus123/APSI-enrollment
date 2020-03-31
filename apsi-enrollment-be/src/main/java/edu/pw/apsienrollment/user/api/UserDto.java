package edu.pw.apsienrollment.user.api;

import edu.pw.apsienrollment.user.db.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class UserDto {
    Integer id;
    String username;
    String name;
    String surname;
    String email;

    static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .build();
    }
}
