package org.librarymanagement.dto.response;

import org.librarymanagement.constant.BRStatusConstant;

import java.time.LocalDateTime;
import java.util.List;

public record BorrowRequestDetailDto(
        String borrowerName,
        String borrowerEmail,
        String borrowerPhone,
        Integer borrowerStatus,

        List<BorrowRequestItemDto> items,

        LocalDateTime startDate,
        LocalDateTime endDate,
        BRStatusConstant status
) {
}
