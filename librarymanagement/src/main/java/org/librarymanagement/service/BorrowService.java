package org.librarymanagement.service;

import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BorrowService {
    ResponseObject borrowBook(Map<Integer, Integer> bookBorrows, User user);
    Page<BorrowRequestSummaryDto> getAllRequests(Integer status, Pageable pageable);
}
