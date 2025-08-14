package org.librarymanagement.mapper;

import org.librarymanagement.entity.EmailTemplate;
import org.librarymanagement.entity.EmailType;

public class EmailTemplateMapper {
    public static EmailTemplate getTemplateByType(EmailType type) {
        return switch (type) {
            case VERIFICATION -> EmailTemplate.VERIFICATION;
            case RESET_PASSWORD -> EmailTemplate.RESET_PASSWORD;
        };
    }
}
