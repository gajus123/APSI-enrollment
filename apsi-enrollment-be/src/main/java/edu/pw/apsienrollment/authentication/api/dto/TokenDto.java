package edu.pw.apsienrollment.authentication.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenDto {
    @Builder.Default
    String tokenType = "Bearer";

    String authToken;
    String refreshToken;
}
