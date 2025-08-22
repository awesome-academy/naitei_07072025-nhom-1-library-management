package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.entity.*;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.BorrowRequestService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@Slf4j
public class BorrowRequestServiceImpl implements BorrowRequestService {

    private final MessageSource messageSource;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRequestItemRepository borrowRequestItemRepository;

    private static final int RETURN = 1;
    private static final int PENDING = 2;

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
                        convertBorrowRequestStatusToString(borrowRequest.getStatus()),
                        borrowRequest.getDayConfirmed(),
                        convertBRItemToResponse(borrowRequestItemRepository.findBorrowRequestItemByBorrowRequest(borrowRequest), PENDING)
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

    public ResponseObject getReturnedBorrowRequests(User user) {
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findBorrowRequestByUser(user);

        List<BorrowRequestResponse> returnedBorrowRequests = new ArrayList<>();

        returnedBorrowRequests = borrowRequests.stream()
                .map(borrowRequest -> {

                    List<BorrowRequestItem> borrowRequestItems = borrowRequestItemRepository.findBorrowRequestItemByBorrowRequest(borrowRequest);

                    List<BorrowRequestItem> returnedBorrowRequestItems = borrowRequestItems.stream()
                            .filter(b -> b.getStatus().equals(BRItemStatusConstant.RETURNED))
                            .toList();

                    List<BorrowRequestItemResponse> borrowRequestItemResponses = convertBRItemToResponse(returnedBorrowRequestItems, RETURN);

                    if(borrowRequestItemResponses.isEmpty()) {
                        return null;
                    }

                    return new BorrowRequestResponse(
                            borrowRequest.getId(),
                            borrowRequest.getQuantity(),
                            convertBorrowRequestStatusToString(borrowRequest.getStatus()),
                            borrowRequest.getDayConfirmed(),
                            borrowRequestItemResponses
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        String successMessage = messageSource.getMessage(
                "user.borrowBooks.returnedBook",
                null,
                LocaleContextHolder.getLocale()
        );

        return new ResponseObject(
                successMessage,
                200,
                returnedBorrowRequests
        );
    }

    private List<BorrowRequestItemResponse> convertBRItemToResponse(List<BorrowRequestItem> borrowRequestItems, int functionType) {

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

                    return switch (functionType) {
                        case RETURN -> new BorrowRequestItemResponse(
                                borrowRequestItem.getId(),
                                convertBRItemStatusToString(borrowRequestItem.getStatus()),
                                new BookVersionResponse(
                                        convertBookVersionStatusToString(bookVersion.getStatus()),
                                        book.getTitle(),
                                        publisherName,
                                        createReviewLink(book.getSlug())
                                )
                        );
                        case PENDING -> new BorrowRequestItemResponse(
                                borrowRequestItem.getId(),
                                convertBRItemStatusToString(borrowRequestItem.getStatus()),
                                new BookVersionResponse(
                                        convertBookVersionStatusToString(bookVersion.getStatus()),
                                        book.getTitle(),
                                        publisherName,
                                        null
                                )
                        );
                        default -> null;
                    };
                })
                .filter(Objects::nonNull)
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

    private String convertBorrowRequestStatusToString(int status){
        return switch (status) {
            case BRStatusConstant.PENDING -> "PENDING";
            case BRStatusConstant.COMPLETED -> "COMPLETED";
            case BRStatusConstant.CANCELLED -> "CANCELLED";
            default -> "";
        };
    }
}
