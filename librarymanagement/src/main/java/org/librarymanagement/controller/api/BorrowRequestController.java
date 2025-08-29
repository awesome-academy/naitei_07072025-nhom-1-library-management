package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.BorrowFlatResponse;
import org.librarymanagement.dto.response.PageResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.BookService;
import org.librarymanagement.service.BorrowRequestService;
import org.librarymanagement.service.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userBorrowRequestController")
@RequestMapping(ApiEndpoints.USER_BORROW_REQUEST)
public class BorrowRequestController {

    private final BorrowRequestService borrowRequestService;
    private final CurrentUserService currentUserService;

    private final MessageSource messageSource;

    @Autowired
    public BorrowRequestController(BorrowRequestService borrowRequestService,
                                   CurrentUserService currentUserService,
                                   MessageSource messageSource) {
        this.borrowRequestService = borrowRequestService;
        this.currentUserService = currentUserService;
        this.messageSource = messageSource;
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseObject> getPendingBorrowRequest() {

        User user = currentUserService.getCurrentUser();

        ResponseObject responseObject = borrowRequestService.getPendingBorrowRequests(user);

        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowFlatResponse>> getReturnedBooks(Pageable pageable) {

        User user = currentUserService.getCurrentUser();

        Page<BorrowFlatResponse> books = borrowRequestService.getReturnedBorrowRequests(user, pageable);

        String successMessage = messageSource.getMessage(
                "user.borrowBooks.returnedBook",
                null,
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.ok(new PageResponse<>(
                successMessage,
                HttpStatus.OK.value(),
                books.getContent(),
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages()
        ));
    }
}
