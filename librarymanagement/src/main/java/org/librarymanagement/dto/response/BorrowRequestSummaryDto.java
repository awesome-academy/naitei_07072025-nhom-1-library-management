package org.librarymanagement.dto.response;

import java.time.LocalDateTime;

public record BorrowRequestSummaryDto(
        Integer id,
        String username,
        Integer totalBooks,
        LocalDateTime borrowDate,
        Integer status
) {}
