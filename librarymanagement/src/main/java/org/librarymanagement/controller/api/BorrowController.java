package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BorrowHistoryDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.BookService;
import org.librarymanagement.service.BorrowHistoryService;
import org.librarymanagement.service.BorrowService;
import org.librarymanagement.service.CurrentUserService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(ApiEndpoints.USER_BORROW_REQUEST)
public class BorrowController {
    private final BorrowHistoryService borrowHistoryService;
    private final BookService  bookService;
    private final BorrowService borrowService;
    private final CurrentUserService  currentUserService;
    private final MessageSource messageSource;

    public BorrowController(BorrowHistoryService borrowHistoryService, BookService bookService, MessageSource messageSource, BorrowService borrowService, CurrentUserService currentUserService) {
        this.borrowHistoryService = borrowHistoryService;
        this.bookService = bookService;
        this.messageSource = messageSource;
        this.borrowService = borrowService;
        this.currentUserService = currentUserService;
    };

    @PostMapping("/borrow")
    public ResponseEntity<ResponseObject> borrowBook(@RequestBody Map<Integer,Integer> borrowRequest, Locale locale) {
        User user = currentUserService.getCurrentUser();

        ResponseObject borrowResponse = borrowService.borrowBook(borrowRequest, user);
        return ResponseEntity.status(borrowResponse.status())
                .body(borrowResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseObject> getBorrowHistory(Locale locale){
        List<BorrowHistoryDto> history = borrowHistoryService.getBorrowHistoryForCurrentUser(locale);
        String message = messageSource.getMessage("user.borrow.history", null, locale);
        ResponseObject responseObject = new ResponseObject(message, HttpStatus.OK.value(), history);

        return ResponseEntity.ok(responseObject);
    }
}
