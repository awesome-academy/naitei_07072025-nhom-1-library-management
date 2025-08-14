package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.entity.User;
import org.librarymanagement.exception.DuplicateFieldException;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.RegisterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RegisterServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Autowired
    @Lazy
    public PasswordEncoder passwordEncoder;

    public Map<String, String> checkDuplicated(RegisterUserDto registerUserDto) {
        Map<String, String> errors = new HashMap<>();

        Map<String,Boolean> checks = Map.of(
                "username" , userRepository.existsUserByUsername(registerUserDto.getUsername()),
                "email" , userRepository.existsUserByEmail(registerUserDto.getEmail()),
                "phone" , userRepository.existsUserByPhone(registerUserDto.getPhone())
        );

        Map<String,String> messages = Map.of(
                "username" , "Username is already taken!",
                "email" , "Email already exists",
                "phone" , "Phone already exists"
        );

        checks.forEach((error,exists) -> {
            if(exists)
            {
                errors.put(error,messages.get(error));
            }
        });
        return errors;
    }

    @Override
    public void saveUser(User user) {
        if (user.getRole() == null)
            user.setRole(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void registerUser(RegisterUserDto registerUserDto) {

        // check lỗi đã tồn tại dữ liệu và trả ra thông báo lỗi
        Map<String, String> errors = checkDuplicated(registerUserDto);
        if (!errors.isEmpty()) {
            throw new DuplicateFieldException(errors);
        }
        User user = modelMapper.map(registerUserDto, User.class);
        saveUser(user);
    }
}
