package org.librarymanagement.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {
    private final LoginService loginService;

    public AdminAuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginUserDto", new LoginUserDto());
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginUserDto") LoginUserDto loginUserDto,
                                    BindingResult bindingResult,
                                    HttpServletRequest request,
                                    Model model) {
        System.out.println("Received username: " + loginUserDto.getUsername());
        System.out.println("Received password: " + loginUserDto.getPassword());
        if (bindingResult.hasErrors()) {
            // Đẩy lỗi ra view
            model.addAttribute("org.springframework.validation.BindingResult.loginUserDto", bindingResult);
            model.addAttribute("loginUserDto", loginUserDto);
            return "admin/login";
        }
        LoginResponseDto loginResponseDto = loginService.login(loginUserDto);

        if (loginResponseDto.isSuccess() && loginResponseDto.getRole()== 1) {
            // Tạo danh sách role với prefix "ROLE_"
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            // Tạo Authentication với username và role
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    loginResponseDto.getUsername(),
                    null,
                    authorities
            );

            // Đặt Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Login thành công, set session
            HttpSession session = request.getSession(true);
            session.setAttribute("username", loginResponseDto.getUsername());

            request.getSession(true).setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext()
            );

            return "redirect:/admin/dashboard";
        } else if (loginResponseDto.isSuccess()) {
            // Đăng nhập bằng tài khoản user
            model.addAttribute("errorMessage", "Bạn không có quyền truy cập");
        } else {
            // Đăng nhập thất bại
            model.addAttribute("errorMessage", loginResponseDto.getMessage());
        }
        return "admin/login";
    }
}
