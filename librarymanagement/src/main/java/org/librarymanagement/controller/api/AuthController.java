package org.librarymanagement.controller.api;

import jakarta.validation.Valid;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.service.RegisterService;
import org.librarymanagement.service.impl.AuthServiceImpl;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController("userAuthController")
@RequestMapping(ApiEndpoints.USER_AUTH)
public class AuthController {
    private final AuthServiceImpl loginService;
    private final RegisterService userService;
    private final MessageSource messageSource;

    public AuthController(AuthServiceImpl loginService , RegisterService userService, MessageSource messageSource) {
        this.loginService = loginService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        LoginResponseDto response = loginService.login(loginUserDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/register")
    ResponseEntity<ResponseObject> registerUser(@RequestBody @Valid RegisterUserDto registerUserDto){
        userService.registerUser(registerUserDto);
        String successMessage = messageSource.getMessage("user.registration.success", null, Locale.getDefault());
        return ResponseEntity.ok( new ResponseObject(
                successMessage,
                HttpStatus.OK.value(),
                registerUserDto
        ));
    }

}
