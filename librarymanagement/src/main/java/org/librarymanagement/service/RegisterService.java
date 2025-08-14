package org.librarymanagement.service;

import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.entity.User;

public interface RegisterService {
    void registerUser(RegisterUserDto registerUserDto);
    void saveUser(User user);
}
