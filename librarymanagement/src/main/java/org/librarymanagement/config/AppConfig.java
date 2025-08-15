package org.librarymanagement.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class AppConfig {

    // chuyển đổi DTO
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");  // Đặt tên file properties (không cần phần mở rộng .properties)
        messageSource.setDefaultEncoding("UTF-8");  // Đảm bảo sử dụng đúng mã hóa
        messageSource.setUseCodeAsDefaultMessage(true); // Nếu thiếu key sẽ hiển thị key luôn
        return messageSource;
    }
}
