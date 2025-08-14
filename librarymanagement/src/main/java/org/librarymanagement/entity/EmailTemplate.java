package org.librarymanagement.entity;

public enum EmailTemplate {
    VERIFICATION(
            "Xác thực email",
            "/req/signup/verify",
            "Nhấn để xác thực email"),
    RESET_PASSWORD(
            "Khôi phục mật khẩu",
            "/req/password/reset",
            "Nhấn để đổi mật khẩu");

    private final String subject;
    private final String path;
    private final String message;

    EmailTemplate(String subject, String path, String message) {
        this.subject = subject;
        this.path = path;
        this.message = message;
    }

    public String getSubject() { return subject; }
    public String getPath() { return path; }
    public String getMessage() { return message; }
}
