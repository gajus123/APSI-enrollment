package edu.pw.apsienrollment.db;

import org.springframework.data.repository.CrudRepository;

import edu.pw.apsienrollment.db.User;

public interface UserRepository extends CrudRepository<User, Integer> {}
