package org.librarymanagement.service.impl;

import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.entity.*;
import org.librarymanagement.repository.BookVersionRepository;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.service.BRCleanupService;
import org.librarymanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class BRCleanupServiceImpl implements BRCleanupService {
    private final BorrowRequestItemRepository borrowRequestItemRepository;
    private final BookVersionRepository bookVersionRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Autowired
    public BRCleanupServiceImpl(BorrowRequestItemRepository borrowRequestItemRepository, BookVersionRepository bookVersionRepository,
                                BorrowRequestRepository borrowRequestRepository,EmailService emailService, MessageSource messageSource) {
        this.borrowRequestItemRepository = borrowRequestItemRepository;
        this.bookVersionRepository = bookVersionRepository;
        this.borrowRequestRepository = borrowRequestRepository;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    //Trường hợp gửi lại thông báo sau 7 ngày vẫn chưa trả sách
   @Scheduled(fixedRate = 604800000)
    public void sendReminderMailForLongOverdue()
    {
        List<BorrowRequest> overdueBorrows = borrowRequestRepository.findByStatuses(
                        Arrays.asList(BRStatusConstant.OVERDUE.getValue()))
                .stream()
                .toList();

        String message = messageSource.getMessage("user.overdueBorrowRequestRepeat.message", null, Locale.getDefault());
        overdueBorrows.forEach(br -> {
            // Nếu chưa gửi mail trong 7 ngày qua thì gửi lại
            if(br.getLastReminderSentAt() == null
                    ||  Duration.between(br.getLastReminderSentAt(), LocalDateTime.now()).toDays() >= 7)
            {
                handleSendMail(br,EmailType.OVERDUE_BORROW_REQUEST_REPEAT);
                br.setLastReminderSentAt(LocalDateTime.now());
            }
        });
        borrowRequestRepository.saveAll(overdueBorrows);
    }

    // Truong hop bi quá hạn mượn
    @Scheduled(fixedRate = 86400000)
    public void checkOverdueBorrowRequests()
    {
        List<BorrowRequest> overdueBorrows = borrowRequestRepository.findByStatuses(
                        Arrays.asList(BRStatusConstant.COMPLETED.getValue()))
                .stream()
                .filter(br -> Duration.between(br.getDayConfirmed(), LocalDateTime.now()).toDays() >= 7)
                .toList();
        String message = messageSource.getMessage("user.overdueBorrowRequest.message", null, Locale.getDefault());
        overdueBorrows.forEach(br -> {
            br.setStatus(BRStatusConstant.OVERDUE.getValue());
            handleSendMail(br,EmailType.OVERDUE_BORROW_REQUEST);
            br.setLastReminderSentAt(LocalDateTime.now()); // them field de luu lại thoi gian gui mail cuoi cung
        });

        borrowRequestRepository.saveAll(overdueBorrows);
    }

    // Trường hợp bị quá time out giữ sách
    @Scheduled(fixedRate = 86400000)
    public void checkReservedOverdueBorrowRequests() {

        // Tim tat ca cac phieu muon co status la dang cho va dang duoc muon
        List<BorrowRequest> overdueBorrows = borrowRequestRepository.findByStatuses(
                Arrays.asList(BRStatusConstant.PENDING.getValue()))
                .stream()
                .filter(br ->  Duration.between(br.getCreatedAt(), LocalDateTime.now()).toDays() >= 3)
                .toList();

        String message = messageSource.getMessage("user.reservedOverdueBorrowRequest.message", null, Locale.getDefault());
        overdueBorrows.forEach(br ->{
            handleSendMail(br,EmailType.RESERVED_OVERDUE_BORROW_REQUEST);
            br.setStatus((BRStatusConstant.CANCELED.getValue()));

            br.getBorrowRequestItems().forEach(brItem -> {
                brItem.setStatus((BRItemStatusConstant.CANCELLED));
                brItem.getBookVersion().setStatus((BookVersionConstants.AVAILABLE));
            });
        });

        borrowRequestRepository.saveAll(overdueBorrows);

        bookVersionRepository.saveAll(
                overdueBorrows.stream()
                        .flatMap(br -> br.getBorrowRequestItems().stream())
                        .map(BorrowRequestItem::getBookVersion)
                        .toList()
        );
    }

    private void handleSendMail(BorrowRequest borrowRequest, EmailType type)
    {
        User user = borrowRequest.getUser();
        String template = "templates/mail/reminderBook_email.html";
        emailService.sendEmailTemp(user.getEmail(), type, template);
    }
}
