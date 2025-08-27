package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(ApiEndpoints.ADMIN_DASHBOARD)
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String showDashboard(Model model) {
        Map<String, Long> userChartData = dashboardService.getUserCountLast12Months();

        // Tách nhãn và dữ liệu cho chart
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        userChartData.forEach((k, v) -> {
            // Lấy chỉ tháng làm nhãn, ví dụ "08/2025"
            labels.add(k.substring(5) + "/" + k.substring(0, 4));
            data.add(v);
        });

        model.addAttribute("userChartLabels", labels);
        model.addAttribute("userChartData", data);
        return "admin/dashboard"; // Thymeleaf template
    }
}
