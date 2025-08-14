package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.entity.EmailTemplate;
import org.librarymanagement.entity.EmailType;
import org.librarymanagement.mapper.EmailTemplateMapper;
import org.librarymanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String email, String token, EmailType type) {
        EmailTemplate template = EmailTemplateMapper.getTemplateByType(type);

        try {
            String actionUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(template.getPath())
                    .queryParam("token", token)
                    .toUriString();

            String content = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                    <h2 style="color: #333;">%s</h2>
                    <p style="font-size: 16px; color: #555;">%s</p>
                    <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #0d7bff; text-decoration: none; border-radius: 5px;">Click Here</a>
                    <p style="font-size: 14px; color: #777;">Or copy and paste this link into your browser:</p>
                    <p style="font-size: 14px; color: #0d7bff;">%s</p>
                    <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                </div>
            """.formatted(template.getSubject(), template.getMessage(), actionUrl, actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(template.getSubject());
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);

        } catch (Exception e){
            log.error("Không thể gửi email tới địa chỉ: {}. Lỗi: {}", email, e.getMessage(), e);
        }
    }
}
