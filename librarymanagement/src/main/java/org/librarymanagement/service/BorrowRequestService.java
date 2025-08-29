package org.librarymanagement.service;

import org.librarymanagement.dto.response.BorrowFlatResponse;
import org.librarymanagement.dto.response.PageResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRequestService {
    public ResponseObject getPendingBorrowRequests(User user);
    public Page<BorrowFlatResponse> getReturnedBorrowRequests(User user, Pageable pageable);
}
