package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.service.BorrowService;
import org.librarymanagement.service.DashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(ApiEndpoints.ADMIN_DASHBOARD)
public class DashboardController {
    private final DashboardService dashboardService;
    private final BorrowService borrowService;

    public DashboardController(DashboardService dashboardService, BorrowService borrowService) {
        this.dashboardService = dashboardService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public String showDashboard(Model model) {
        // Load Chart borrowCounts
        Map<String, Long> borrowCounts = dashboardService.getBorrowCountLast12Months();

        // Tách nhãn và dữ liệu cho chart
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        borrowCounts.forEach((k, v) -> {
            labels.add(k.substring(5) + "/" + k.substring(0, 4));
            data.add(v);
        });

        model.addAttribute("labels", labels);
        model.addAttribute("data", data);

        // Load table borrowRequest
        Page<BorrowRequestSummaryDto> borrowRequests =
                borrowService.getAllRequests(null, PageRequest.of(0, 8));
        model.addAttribute("borrowRequests", borrowRequests.getContent());
        return "admin/dashboard";
    }
}
