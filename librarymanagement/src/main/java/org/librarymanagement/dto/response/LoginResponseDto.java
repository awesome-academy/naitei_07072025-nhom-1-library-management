package org.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private int role;

    public LoginResponseDto(boolean success, String message, String token, String username, int role) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
