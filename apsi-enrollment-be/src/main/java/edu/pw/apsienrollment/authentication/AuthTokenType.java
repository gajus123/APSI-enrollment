package edu.pw.apsienrollment.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum AuthTokenType {
    ACCESS_TOKEN(Duration.ofHours(2)),
    REFRESH_TOKEN(Duration.ofDays(14));

    private final Duration validityPeriod;
}
