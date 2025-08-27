package org.librarymanagement.service.impl;

import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;

    public DashboardServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Long> getUserCountLast12Months() {
        // Lấy kết quả từ DB
        List<Object[]> result = userRepository.countNewUsersLast12Months();

        // Tạo map 12 tháng gần nhất với 0
        Map<String, Long> monthCountMap = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String key = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            monthCountMap.put(key, 0L);
        }

        // Đưa dữ liệu từ DB vào map
        for (Object[] row : result) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            Long count = ((Number) row[2]).longValue();
            String key = year + "-" + String.format("%02d", month);

            if (monthCountMap.containsKey(key)) {
                monthCountMap.put(key, count);
            }
        }

        return monthCountMap;
    }
}
