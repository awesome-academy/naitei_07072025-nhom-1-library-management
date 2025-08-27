package org.librarymanagement.service.impl;

import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final BorrowRequestRepository borrowRequestRepository;

    public DashboardServiceImpl(BorrowRequestRepository borrowRequestRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
    }

    public Map<String, Long> getBorrowCountLast12Months() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(11).withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        List<Object[]> result = borrowRequestRepository.countBorrowRequestsLast12Months(
                start.atStartOfDay(), end.atTime(23,59,59));

        Map<String, Long> monthCountMap = new LinkedHashMap<>();

        // Tạo map 12 tháng gần nhất với 0
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
