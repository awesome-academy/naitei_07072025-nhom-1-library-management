package org.librarymanagement.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.entity.BorrowRequestItem;
import org.librarymanagement.service.BorrowRequestItemService;
import org.librarymanagement.service.BorrowRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminBorrowRequestItémController")
@RequestMapping(ApiEndpoints.ADMIN_BORROW_REQUEST_ITEM)
public class BorrowRequestItemController {
    private final BorrowRequestItemService borrowRequestItemService;

    public BorrowRequestItemController(BorrowRequestItemService borrowRequestItemService) {
        this.borrowRequestItemService = borrowRequestItemService;
    }

    @PatchMapping("/return")
    public String returnRequestItem(@RequestParam("itemId") Integer itemId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        borrowRequestItemService.returnRequestItem(itemId);
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            // Fallback to a default page if the referer is not available
            referer = ApiEndpoints.ADMIN_BORROW_REQUEST;
        }
        redirectAttributes.addFlashAttribute("message", "Trả sách thành công");
        redirectAttributes.addFlashAttribute("alertType", "success");
        return "redirect:" + referer ;
    }
}
