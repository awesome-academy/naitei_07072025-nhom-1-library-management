package org.librarymanagement.service.impl;

import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BorrowRuleConstant;
import org.librarymanagement.dto.response.BorrowHistoryDto;
import org.librarymanagement.entity.BorrowRequestItem;
import org.librarymanagement.entity.User;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.service.BorrowHistoryService;
import org.librarymanagement.service.CurrentUserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BorrowHistoryServiceImpl implements BorrowHistoryService {
    private final BorrowRequestItemRepository borrowRequestItemRepository;
    private final CurrentUserService currentUserService;
    private final MessageSource messageSource;

     public BorrowHistoryServiceImpl( BorrowRequestItemRepository borrowRequestItemRepository, CurrentUserService currentUserService, MessageSource messageSource) {
         this.borrowRequestItemRepository = borrowRequestItemRepository;
         this.currentUserService = currentUserService;
         this.messageSource = messageSource;
     }

    @Override
    public List<BorrowHistoryDto> getBorrowHistoryForCurrentUser(Locale locale){
        User currentUser = currentUserService.getCurrentUser();

        List<BorrowRequestItem> historyItems = borrowRequestItemRepository.findBorrowBookByUserId(currentUser.getId());

        return historyItems.stream()
                .map(item -> convertToDto(item, locale))
                .collect(Collectors.toList());
    }

    private BorrowHistoryDto convertToDto(BorrowRequestItem item, Locale locale) {
        String derivedStatus = calculateDerivedStatus(item, locale);

        return new BorrowHistoryDto(
                item.getBookVersion().getBook().getTitle(),
                item.getBookVersion().getBook().getImage(),
                item.getBookVersion().getBook().getSlug(),
                item.getCreatedAt(),
                item.getDayExpired(),
                item.getDayReturn(),
                item.getStatus(),
                derivedStatus
        );
    }

    private String calculateDerivedStatus(BorrowRequestItem item, Locale locale) {
        int status = item.getStatus();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayExpired = item.getDayExpired();

        //Sách bị mất
        if (status == BRItemStatusConstant.LOST) {
            return messageSource.getMessage("status.lost", null, locale);
        }

        //Đang mượn/sắp hết hạn/quá hạn
        if (status == BRItemStatusConstant.BORROWED || status == BRItemStatusConstant.OVERDUE) {
            if (dayExpired.isBefore(now)) {
                return messageSource.getMessage("status.overdue", null, locale);
            }

            long daysUntilDue = ChronoUnit.DAYS.between(now, dayExpired);

            if (daysUntilDue <= BorrowRuleConstant.DUE_SOON_DAY) {
                return messageSource.getMessage("status.dueSoon", null, locale);
            }
            return messageSource.getMessage("status.borrowing", null, locale);
        }

        return messageSource.getMessage("status.unknown", null, locale);
    }
}
