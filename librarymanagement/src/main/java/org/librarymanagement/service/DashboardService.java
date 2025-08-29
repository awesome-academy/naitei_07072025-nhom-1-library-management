package org.librarymanagement.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Long> getUserCountLast12Months();
}
