package edu.pw.apsienrollment.user;

import edu.pw.apsienrollment.user.db.User;

public interface UserService {
    User login(String email, String password);

    String getUsersToken(User user);

    User getAuthenticatedUser();

    User getUser(Integer id);
}
