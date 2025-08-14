package org.librarymanagement.security;

import org.librarymanagement.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Getter để Thymeleaf dùng
    public String getAvatar() {
        return user.getAvatar();
    }

    public String getName() {
        return user.getName();
    }

    public Integer getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Integer getRoleValue() {
        return user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ví dụ: 1 = ROLE_ADMIN, 2 = ROLE_USER
        String roleName = switch (user.getRole()) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_USER";
            default -> "ROLE_GUEST";
        };
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Bạn có thể dùng status để kiểm tra
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Bạn có thể dùng activatedStatus để kiểm tra
    }

    @Override
    public boolean isEnabled() {
        return user.getActivatedStatus() != null && user.getActivatedStatus() == 1;
    }
}
