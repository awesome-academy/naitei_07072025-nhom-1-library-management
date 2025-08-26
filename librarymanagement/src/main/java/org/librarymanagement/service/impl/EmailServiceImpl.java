package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.entity.EmailTemplate;
import org.librarymanagement.entity.EmailType;
import org.librarymanagement.mapper.EmailTemplateMapper;
import org.librarymanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String email, String token, EmailType type) {
        EmailTemplate template = EmailTemplateMapper.getTemplateByType(type);

        try {
            String content = readEmailTemplate("templates/email/verification-email.html");

            //Tạo URL hành động
            String actionUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(template.getPath())
                    .queryParam("token", token)
                    .toUriString();

            //Thay thế các biến giữ chỗ trong template
            content = content.replace("${subject}", template.getSubject());
            content = content.replace("${message}", template.getMessage());
            content = content.replace("${actionUrl}", actionUrl);
            //Thay thế các biến giữ chỗ trong template
            content = content.replace("${subject}", template.getSubject());
            content = content.replace("${message}", template.getMessage());
            content = content.replace("${actionUrl}", actionUrl);

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

    public void sendEmailTemp(String email,EmailType type, String url)
    {
        EmailTemplate template = EmailTemplateMapper.getTemplateByType(type);

        try {
            String content = readEmailTemplate(url);

            //Tạo URL hành động

            //Thay thế các biến giữ chỗ trong template
            content = content.replace("${subject}", template.getSubject());
            content = content.replace("${message}", template.getMessage());

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

    private String readEmailTemplate(String path) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
