package org.librarymanagement.mapper;

import org.librarymanagement.entity.EmailTemplate;
import org.librarymanagement.entity.EmailType;

public class EmailTemplateMapper {
    public static EmailTemplate getTemplateByType(EmailType type) {
        return switch (type) {
            case VERIFICATION -> EmailTemplate.VERIFICATION;
            case RESET_PASSWORD -> EmailTemplate.RESET_PASSWORD;
            case OVERDUE_BORROW_REQUEST ->  EmailTemplate.OVERDUE_BORROW_REQUEST;
            case OVERDUE_BORROW_REQUEST_REPEAT ->   EmailTemplate.OVERDUE_BORROW_REQUEST_REPEAT;
            case RESERVED_OVERDUE_BORROW_REQUEST ->   EmailTemplate.RESERVED_OVERDUE_BORROW_REQUEST;
        };
    }
}
