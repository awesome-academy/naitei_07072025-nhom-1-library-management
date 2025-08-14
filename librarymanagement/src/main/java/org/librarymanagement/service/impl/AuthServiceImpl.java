package org.librarymanagement.service.impl;

import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.entity.User;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.AuthService;
import org.librarymanagement.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponseDto login(LoginUserDto loginUserDto) {
        // Tìm user theo username
        User user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElse(null);

        if (user == null) {
            return new LoginResponseDto(false, "Tài khoản không tồn tại", null, null, -1);

        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            return new LoginResponseDto(false, "Sai mật khẩu", null, null, -1);
        }

        // Tạo JWT token
        String token = jwtUtil.generateLoginToken(user.getUsername());

        return new LoginResponseDto(true, "Đăng nhập thành công", token, user.getUsername(), user.getRole());
    }
}
