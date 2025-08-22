package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;

public interface BorrowRequestService {
    public ResponseObject getPendingBorrowRequests(User user);
    public ResponseObject getReturnedBorrowRequests(User user);
}
