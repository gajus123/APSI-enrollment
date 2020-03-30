package edu.pw.apsienrollment.user.api;

import edu.pw.apsienrollment.user.UserService;
import edu.pw.apsienrollment.user.api.dto.AuthTokenDto;
import edu.pw.apsienrollment.user.api.dto.CredentialsDto;
import edu.pw.apsienrollment.user.api.dto.UserDto;
import edu.pw.apsienrollment.user.db.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "Log in", nickname = "login", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = AuthTokenDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @PostMapping("login")
    ResponseEntity<AuthTokenDto> login(@RequestBody CredentialsDto registrationDto) {
        User authenticated = userService.login(registrationDto.getUsername(), registrationDto.getPassword());
        return ResponseEntity.ok(new AuthTokenDto(userService.getUsersToken(authenticated)));
    }

    @ApiOperation(value = "Get user info", nickname = "get user info", notes = "",
            authorizations = {@Authorization(value = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = UserDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @GetMapping("me")
    ResponseEntity<UserDto> getAuthenticatedUser() {
        return ResponseEntity.ok(UserDto.of(userService.getAuthenticatedUser()));
    }
}
