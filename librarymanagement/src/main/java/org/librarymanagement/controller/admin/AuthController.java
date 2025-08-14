package org.librarymanagement.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.constant.RoleConstants;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.service.AuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller("adminAuthController")
@RequestMapping(ApiEndpoints.ADMIN_AUTH)
public class AuthController {
    private final AuthService loginService;

    public AuthController(AuthService loginService) {
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
        if (bindingResult.hasErrors()) {
            // Đẩy lỗi ra view
            model.addAttribute("org.springframework.validation.BindingResult.loginUserDto", bindingResult);
            model.addAttribute("loginUserDto", loginUserDto);
            return "admin/login";
        }
        LoginResponseDto loginResponseDto = loginService.login(loginUserDto);

        if (!loginResponseDto.success()) {
            populateLoginFormModel(model, loginUserDto, null, loginResponseDto.message());
            return "admin/login";
        }

        if (loginResponseDto.role() != RoleConstants.ADMIN) {
            populateLoginFormModel(model, loginUserDto, null, "Bạn không có quyền truy cập");
            return "admin/login";
        }

        authenticateAdminUser(loginResponseDto, request);
        return "redirect:" + ApiEndpoints.ADMIN_DASHBOARD;
    }

    private void populateLoginFormModel(Model model, LoginUserDto loginUserDto, BindingResult bindingResult, String errorMessage) {
        if (bindingResult != null) {
            model.addAttribute("org.springframework.validation.BindingResult.loginUserDto", bindingResult);
        }
        model.addAttribute("loginUserDto", loginUserDto);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
    }

    private void authenticateAdminUser(LoginResponseDto loginResponseDto, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginResponseDto.username(), null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", loginResponseDto);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }
}
