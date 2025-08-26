package org.librarymanagement.entity;

public enum EmailTemplate {
    VERIFICATION(
            "Xác thực email",
            "api/auth/register/verify",
            "Nhấn để xác thực email"),
    RESET_PASSWORD(
            "Khôi phục mật khẩu",
            "api/auth/register/password/reset",
            "Nhấn để đổi mật khẩu"),

    OVERDUE_BORROW_REQUEST(
            "Thông báo",
            "",
            "Bạn đã quá hạn thời gian mượn sách, hay mang đến trả thư viện"
    ),

    OVERDUE_BORROW_REQUEST_REPEAT(
            "Cảnh cáo",
            "",
            "Bạn vẫn chưa trả sách , hay mang đến trả thư viện !!"
    ),

    RESERVED_OVERDUE_BORROW_REQUEST(
            "Thông báo",
            "",
            "Yêu cầu mượn sách của bạn bị hủy do quá thời gian giữ sách !!"
    );



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
