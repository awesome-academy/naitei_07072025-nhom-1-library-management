package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.entity.*;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.service.BorrowRequestService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.librarymanagement.constant.BRItemStatusConstant.CANCELLED;
import static org.librarymanagement.constant.BRItemStatusConstant.PENDING;
import static org.librarymanagement.constant.BRStatusConstant.COMPLETED;
import static org.librarymanagement.constant.BRStatusConstant.fromValue;

@Service
@Transactional(readOnly=true)
@Slf4j
public class BorrowRequestServiceImpl implements BorrowRequestService {

    private final MessageSource messageSource;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRequestItemRepository borrowRequestItemRepository;

    public BorrowRequestServiceImpl(MessageSource messageSource,
                                    BorrowRequestRepository borrowRequestRepository,
                                    BorrowRequestItemRepository borrowRequestItemRepository) {
        this.messageSource = messageSource;
        this.borrowRequestRepository = borrowRequestRepository;
        this.borrowRequestItemRepository = borrowRequestItemRepository;
    }

    @Override
    public ResponseObject getPendingBorrowRequests(User user) {

        List<BorrowRequest> borrowRequests = borrowRequestRepository.findBorrowRequestByUser(user);

        List<BorrowRequest> pendingBorrowRequests = new ArrayList<>();

        pendingBorrowRequests = borrowRequests.stream().filter(b -> b.getStatus().equals(BRStatusConstant.PENDING)).toList();

        if(pendingBorrowRequests.isEmpty()) {
            return new ResponseObject(
                    messageSource.getMessage(
                            "user.borrowBooks.noPendingRequests",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    200,
                    null
            );
        }

        List<BorrowRequestResponse> borrowRequestResponses = pendingBorrowRequests.stream()
                .map(borrowRequest -> new BorrowRequestResponse(
                        borrowRequest.getId(),
                        borrowRequest.getQuantity(),
                        fromValue(borrowRequest.getStatus()).getLabel(),
                        borrowRequest.getDayConfirmed(),
                        convertBRItemToResponse(borrowRequestItemRepository.findBorrowRequestItemByBorrowRequest(borrowRequest))
                ))
                .toList();

        String successMessage = messageSource.getMessage(
                "user.borrowBooks.pending",
                null,
                LocaleContextHolder.getLocale()
        );

        return new ResponseObject(
                successMessage,
                200,
                borrowRequestResponses
        );
    }

    @Override
    public ResponseObject getReturnedBorrowRequests(User user) {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findBorrowRequestByUser(user);

        List<BorrowFlatResponse> returnedResponses = new ArrayList<>();

        for (BorrowRequest request : borrowRequests) {
            List<BorrowRequestItem> items = borrowRequestItemRepository.findBorrowRequestItemByBorrowRequest(request).stream()
                    .filter(b -> b.getStatus().equals(BRItemStatusConstant.RETURNED))
                    .toList();

            items.forEach(item -> {
                String reviewLink = createReviewLink(item.getBookVersion().getBook().getSlug()).href();
                returnedResponses.add(convertToFlatResponse(item, null, reviewLink));
            });
        }

        if(returnedResponses.isEmpty()) {
            return new ResponseObject(
                    messageSource.getMessage(
                            "user.borrowBooks.noReturnedBook",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    200,
                    null
            );
        }

        String successMessage = messageSource.getMessage(
                "user.borrowBooks.returnedBook",
                null,
                LocaleContextHolder.getLocale()
        );

        return new ResponseObject(
                successMessage,
                200,
                returnedResponses
        );
    }

    private BorrowFlatResponse convertToFlatResponse(BorrowRequestItem item, String cancelReason, String reviewLink) {
        BookVersion bookVersion = item.getBookVersion();
        Book book = bookVersion.getBook();
        Publisher publisher = book.getPublisher();
        String publisherName = (publisher != null) ? publisher.getName() : "N/A";

        return new BorrowFlatResponse(
                item.getBorrowRequest().getId(),
                item.getId(),
                book.getTitle(),
                publisherName,
                convertBRItemStatusToString(item.getStatus()),
                fromValue(item.getBorrowRequest().getStatus()).getLabel(),
                reviewLink,
                cancelReason
        );
    }

    private List<BorrowRequestItemResponse> convertBRItemToResponse(List<BorrowRequestItem> borrowRequestItems) {

        if (borrowRequestItems == null) {
            return new ArrayList<>();
        }

        List<BorrowRequestItemResponse> borrowRequestItemResponses = new ArrayList<>();

        borrowRequestItemResponses = borrowRequestItems.stream()
                .map(borrowRequestItem -> {
                    BookVersion bookVersion = borrowRequestItem.getBookVersion();
                    if (bookVersion == null) {
                        throw new NotFoundException("Không thể tìm thấy book version");
                    }

                    Book book = bookVersion.getBook();
                    if (book == null) {
                        throw new NotFoundException("Không thể tìm thấy book");
                    }

                    Publisher publisher = book.getPublisher();
                    String publisherName = (publisher != null) ? publisher.getName() : "N/A";

                    return new BorrowRequestItemResponse(
                            borrowRequestItem.getId(),
                            convertBRItemStatusToString(borrowRequestItem.getStatus()),
                            new BookVersionResponse(
                                    convertBookVersionStatusToString(bookVersion.getStatus()),
                                    book.getTitle(),
                                    publisherName
                            )
                    );
                })
                .toList();

        return borrowRequestItemResponses;
    }

    private LinkResponse createReviewLink(String slug){
        String link = new String("http://localhost:8080/api/books/");

        link = link + slug + "/reviews";

        return new LinkResponse(
                link,
                "reviews",
                "Hãy đánh giá sách này"
        );
    }

    private String convertBRItemStatusToString(int status){
        return switch (status) {
            case BRItemStatusConstant.PENDING -> "PENDING";
            case BRItemStatusConstant.BORROWED -> "BORROWED";
            case BRItemStatusConstant.RETURNED -> "RETURNED";
            case BRItemStatusConstant.OVERDUE -> "OVERDUE";
            case BRItemStatusConstant.LOST -> "LOST";
            default -> "";
        };
    }

    private String convertBookVersionStatusToString(int status){
        return switch (status) {
            case BookVersionConstants.DAMAGED -> "DAMAGED";
            case BookVersionConstants.AVAILABLE -> "AVAILABLE";
            case BookVersionConstants.BORROWED -> "BORROWED";
            case BookVersionConstants.RESERVED -> "RESERVED";
            case BookVersionConstants.REPAIRING -> "REPAIRING";
            default -> "";
        };
    }
}
