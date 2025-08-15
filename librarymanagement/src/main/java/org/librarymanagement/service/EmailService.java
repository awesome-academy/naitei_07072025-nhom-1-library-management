package org.librarymanagement.service;

import org.librarymanagement.entity.EmailType;

public interface EmailService {
    void sendEmail(String email, String token, EmailType type);
}
