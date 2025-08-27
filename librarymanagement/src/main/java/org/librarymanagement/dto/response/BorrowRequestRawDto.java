package org.librarymanagement.dto.response;

import org.librarymanagement.constant.BRStatusConstant;

import java.time.LocalDateTime;

public record BorrowRequestRawDto (
        Integer id,
        String username,
        Integer totalBooks,
        LocalDateTime borrowDate,
        Integer status
) {
}
