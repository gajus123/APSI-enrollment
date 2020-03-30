package edu.pw.apsienrollment.user.db;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    STUDENT, LECTURER, SECRETARY;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
