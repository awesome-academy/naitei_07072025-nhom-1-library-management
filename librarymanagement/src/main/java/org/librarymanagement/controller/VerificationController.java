package org.librarymanagement.controller;

import org.librarymanagement.entity.User;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/req/signup/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String emailString = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(emailString);
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Người dùng không tồn tại");
            }
            if (user.getVerificationToken() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Người dùng chưa yêu cầu xác thực email hoặc đã được xác thực");
            }

            if (!user.getVerificationToken().equals(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Mã xác thực không khớp!");
            }

            user.setVerificationToken(null);
            user.setActivatedStatus(true);
            userRepository.save(user);

            return ResponseEntity.ok("Xác thực email thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi trong quá trình xác thực. Vui lòng thử lại sau!");
        }
    }
}
