package org.librarymanagement.service.impl;

import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.dto.response.DashboardStatsDto;
import org.librarymanagement.dto.response.StatDto;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRequestRepository borrowRequestRepository;

    public DashboardServiceImpl(BorrowRequestRepository borrowRequestRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.bookRepository = bookRepository;
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


    public DashboardStatsDto getDashboardData() {
        LocalDateTime startThisWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime startLastWeek = startThisWeek.minusWeeks(1);
        LocalDateTime endLastWeek = startThisWeek.minusSeconds(1);

        // Borrow Requests
        long thisWeekBorrowRequests = borrowRequestRepository.countByCreatedAtAfter(startThisWeek);
        long lastWeekBorrowRequests = borrowRequestRepository.countByCreatedAtBetween(startLastWeek, endLastWeek);
        StatDto borrowRequests = new StatDto(
                thisWeekBorrowRequests,
                calcChange(thisWeekBorrowRequests, lastWeekBorrowRequests)
        );

        // Real Borrows
        long thisWeekRealBorrows = borrowRequestRepository.countByStatusAndCreatedAtAfter(BRStatusConstant.COMPLETED.getValue(), startThisWeek);
        long lastWeekRealBorrows = borrowRequestRepository.countByStatusAndCreatedAtBetween(BRStatusConstant.COMPLETED.getValue(), startLastWeek, endLastWeek);
        StatDto realBorrows = new StatDto(
                thisWeekRealBorrows,
                calcChange(thisWeekRealBorrows, lastWeekRealBorrows)
        );

        // New Books
        long thisWeekNewBooks = bookRepository.countByCreatedAtAfter(startThisWeek);
        long lastWeekNewBooks = bookRepository.countByCreatedAtBetween(startLastWeek, endLastWeek);
        StatDto newBooks = new StatDto(
                thisWeekNewBooks,
                calcChange(thisWeekNewBooks, lastWeekNewBooks)
        );

        // New Members
        long thisWeekNewMembers = userRepository.countByCreatedAtAfter(startThisWeek);
        long lastWeekNewMembers = userRepository.countByCreatedAtBetween(startLastWeek, endLastWeek);
        StatDto newMembers = new StatDto(
                thisWeekNewMembers,
                calcChange(thisWeekNewMembers, lastWeekNewMembers)
        );

        return new DashboardStatsDto(borrowRequests, realBorrows, newBooks, newMembers);
    }
    private double calcChange(long thisWeek, long lastWeek) {
        if (lastWeek == 0) {
            return thisWeek > 0 ? 100.0 : 0.0;
        }
        return ((double) (thisWeek - lastWeek) / lastWeek) * 100.0;
    }


}
