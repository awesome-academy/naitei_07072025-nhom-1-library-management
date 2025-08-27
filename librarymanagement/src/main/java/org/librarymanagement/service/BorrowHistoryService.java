package org.librarymanagement.service;

import org.librarymanagement.dto.response.BorrowHistoryDto;

import java.util.List;
import java.util.Locale;

public interface BorrowHistoryService {
    List<BorrowHistoryDto> getBorrowHistoryForCurrentUser(Locale locale);
}
