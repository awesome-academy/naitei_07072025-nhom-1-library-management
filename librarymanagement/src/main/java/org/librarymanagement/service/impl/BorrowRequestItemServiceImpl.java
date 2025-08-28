package org.librarymanagement.service.impl;

import jakarta.transaction.Transactional;
import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.entity.BookVersion;
import org.librarymanagement.entity.BorrowRequestItem;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.service.BorrowRequestItemService;
import org.springframework.stereotype.Service;

@Service
public class BorrowRequestItemServiceImpl implements BorrowRequestItemService {
    private final BorrowRequestItemRepository borrowRequestItemRepository;

    public BorrowRequestItemServiceImpl(BorrowRequestItemRepository borrowRequestItemRepository) {
        this.borrowRequestItemRepository = borrowRequestItemRepository;
    }

    @Transactional
    public void returnRequestItem(Integer itemId) {
        BorrowRequestItem borrowRequestItem = borrowRequestItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu mượn với id: " + itemId));
        borrowRequestItem.setStatus(BRItemStatusConstant.RETURNED);
        BookVersion bookVersion = borrowRequestItem.getBookVersion();
        bookVersion.setStatus(BookVersionConstants.AVAILABLE);
        borrowRequestItem.setBookVersion(bookVersion);
        borrowRequestItemRepository.save(borrowRequestItem);
    }
}
