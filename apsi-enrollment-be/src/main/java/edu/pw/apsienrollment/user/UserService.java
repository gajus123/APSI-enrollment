package edu.pw.apsienrollment.user;

import edu.pw.apsienrollment.user.db.User;

public interface UserService {
    User getUser(Integer id);

    User getUserByUsername(String username);
}
