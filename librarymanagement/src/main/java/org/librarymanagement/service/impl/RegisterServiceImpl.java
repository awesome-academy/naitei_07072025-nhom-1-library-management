package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.EmailType;
import org.librarymanagement.entity.User;
import org.librarymanagement.exception.DuplicateFieldException;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.EmailService;
import org.librarymanagement.service.RegisterService;
import org.librarymanagement.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private EmailService emailService;

    @Autowired
    public RegisterServiceImpl(UserRepository userRepository, ModelMapper modelMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    @Autowired
    @Lazy
    public PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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
    public String registerUser(RegisterUserDto registerUserDto) {
        User existingUser = userRepository.findByEmail(registerUserDto.getEmail());

        if (existingUser != null) {
            if (existingUser.isActivatedStatus()) {
                // Nếu đã xác thực, thông báo
                return "Người dùng đã tồn tại và đã xác thực";
            } else {
                // Nếu chưa xác thực, thông báo đã gửi lại email
                String newToken = jwtUtil.generateVerificationToken(existingUser.getEmail());
                existingUser.setVerificationToken(newToken);
                userRepository.save(existingUser);
                emailService.sendEmail(existingUser.getEmail(), newToken, EmailType.VERIFICATION);
                return "Mã xác thực đã được gửi qua mail, vui lòng kiểm tra hộp thư";
            }
        }
        // Tạo người dùng mới
        User user = modelMapper.map(registerUserDto, User.class);
        String newToken = jwtUtil.generateVerificationToken(user.getEmail());
        user.setVerificationToken(newToken);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(), newToken, EmailType.VERIFICATION);
        return "Đăng ký thành công, vui lòng xác thực email";
    }
}
