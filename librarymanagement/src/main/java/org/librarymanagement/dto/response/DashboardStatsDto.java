package org.librarymanagement.dto.response;

public record DashboardStatsDto (
        StatDto borrowRequests,
        StatDto realBorrows,
        StatDto newBooks,
        StatDto newMembers
) {}

