package edu.pw.apsienrollment.authentication.api;

import edu.pw.apsienrollment.authentication.AuthTokenType;
import edu.pw.apsienrollment.authentication.AuthenticationService;
import edu.pw.apsienrollment.authentication.api.dto.AccessTokenDto;
import edu.pw.apsienrollment.authentication.api.dto.AuthenticatedUserDto;
import edu.pw.apsienrollment.authentication.api.dto.CredentialsDto;
import edu.pw.apsienrollment.authentication.api.dto.TokenDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @ApiOperation(value = "Authenticate", nickname = "login", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = AccessTokenDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @PostMapping("authenticate")
    ResponseEntity<TokenDto> authenticate(@RequestBody CredentialsDto registrationDto) {
        String authenticated = authenticationService.authenticate(registrationDto.getUsername(), registrationDto.getPassword());
        return ResponseEntity.ok(TokenDto.builder()
                .accessToken(authenticationService.getToken(authenticated, AuthTokenType.ACCESS_TOKEN))
                .refreshToken(authenticationService.getToken(authenticated, AuthTokenType.REFRESH_TOKEN))
                .build());
    }

    @ApiOperation(value = "Refresh token", nickname = "refresh", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = AccessTokenDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @GetMapping("refresh")
    ResponseEntity<AccessTokenDto> refreshAuthentication(@RequestParam String refreshToken) {
        String authenticated = authenticationService.authenticateFromRefreshToken(refreshToken);
        return ResponseEntity.ok(new AccessTokenDto(
                authenticationService.getToken(authenticated, AuthTokenType.ACCESS_TOKEN)));
    }

    @ApiOperation(value = "Get authenticated user info", nickname = "get user info", notes = "",
            authorizations = {@Authorization(value = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = AuthenticatedUserDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @GetMapping("me")
    ResponseEntity<AuthenticatedUserDto> getAuthenticatedUser() {
        return ResponseEntity.ok(AuthenticatedUserDto.of(authenticationService.getAuthenticatedUser()));
    }
}
