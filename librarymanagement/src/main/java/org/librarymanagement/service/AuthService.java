package org.librarymanagement.service;

import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;

public interface AuthService {
        LoginResponseDto login(LoginUserDto loginUserDto);
}
