package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.service.BorrowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("adminBorrowRequestController")
@RequestMapping(ApiEndpoints.ADMIN_BORROW_REQUEST)
public class BorrowRequestController {
    private final BorrowService borrowService;

    public BorrowRequestController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    public String showRequestBook(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<BorrowRequestSummaryDto> borrowRequests =
                borrowService.getAllRequests(status, PageRequest.of(page, size));

        model.addAttribute("borrowRequests", borrowRequests.getContent());
        model.addAttribute("totalPages", borrowRequests.getTotalPages() == 0 ? 1
                : borrowRequests.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        return "admin/borrow-requests/index";
    }

    @GetMapping("/{id}")
    public String showRequestBookedit() {
        return "admin/borrow-requests/detail";
    }
}
