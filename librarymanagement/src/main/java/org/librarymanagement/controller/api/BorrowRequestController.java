package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.BookService;
import org.librarymanagement.service.BorrowRequestService;
import org.librarymanagement.service.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public BorrowRequestController(BorrowRequestService borrowRequestService, CurrentUserService currentUserService) {
        this.borrowRequestService = borrowRequestService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseObject> getPendingBorrowRequest() {

        User user = currentUserService.getCurrentUser();

        ResponseObject responseObject = borrowRequestService.getPendingBorrowRequests(user);

        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }
}
