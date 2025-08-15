package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ApiEndpoints.ADMIN_BOOK)
public class BookController {
    @GetMapping
    public String showBooklist() {
        return "admin/booklist";
    }

    @GetMapping("/request")
    public String showRequestBook() {
        return "admin/requestbooklist";
    }
}
