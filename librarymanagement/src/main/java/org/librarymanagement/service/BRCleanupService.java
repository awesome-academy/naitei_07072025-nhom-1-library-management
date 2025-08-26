package org.librarymanagement.service;

public interface BRCleanupService {
    void checkOverdueBorrowRequests();
    void checkReservedOverdueBorrowRequests();
}
