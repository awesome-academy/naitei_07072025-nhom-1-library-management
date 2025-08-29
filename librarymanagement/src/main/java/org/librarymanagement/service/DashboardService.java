package org.librarymanagement.service;

import java.util.Map;
import org.librarymanagement.dto.response.DashboardStatsDto;

public interface DashboardService {
    Map<String, Long> getUserCountLast12Months();
    DashboardStatsDto getDashboardData();
}
