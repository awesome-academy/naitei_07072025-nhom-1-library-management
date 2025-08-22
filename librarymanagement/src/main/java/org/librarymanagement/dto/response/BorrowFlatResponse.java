package org.librarymanagement.dto.response;

public record BorrowFlatResponse(
        Integer borrowRequestId,
        Integer borrowRequestItemId,
        String bookTitle,
        String publisherName,
        String borrowItemStatus,
        String borrowStatus,
        String reviewLink,
        String cancelReason
) {}
