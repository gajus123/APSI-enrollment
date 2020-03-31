package edu.pw.apsienrollment.user.api;

import edu.pw.apsienrollment.user.UserService;
import edu.pw.apsienrollment.authentication.api.dto.AuthTokenDto;
import edu.pw.apsienrollment.authentication.api.dto.CredentialsDto;
import edu.pw.apsienrollment.authentication.api.dto.AuthenticatedUserDto;
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

    @ApiOperation(value = "Get user info", nickname = "get user info", notes = "",
            authorizations = {@Authorization(value = "JWT")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If valid credentials were provided", response = AuthenticatedUserDto.class),
            @ApiResponse(code = 400, message = "If invalid data was provided")})
    @GetMapping("{id}")
    ResponseEntity<AuthenticatedUserDto> getAuthenticatedUser(@PathVariable Integer id) {
        return ResponseEntity.ok(AuthenticatedUserDto.of(userService.getUser(id)));
    }
}
