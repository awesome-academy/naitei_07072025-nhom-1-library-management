package org.librarymanagement.service.impl;

import jakarta.transaction.Transactional;

import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.constant.RoleConstants;
import org.librarymanagement.dto.response.BorrowRequestRawDto;
import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.dto.response.BorrowResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.*;
import org.librarymanagement.exception.NotEnoughBookException;
import org.librarymanagement.exception.NotEnoughConditionsBorrowException;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.repository.BookVersionRepository;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.librarymanagement.constant.BRStatusConstant.*;

@Service
public class BorrowServiceImpl implements BorrowService {
    private final BookVersionRepository bookVersionRepository;
    private final BorrowRequestItemRepository borrowRequestItemRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final MessageSource messageSource;
    private final BookRepository bookRepository;

    @Autowired
    public BorrowServiceImpl(BookVersionRepository bookVersionRepository,  BorrowRequestItemRepository borrowRequestItemRepository,  MessageSource messageSource, BorrowRequestRepository borrowRequestRepository,   BookRepository bookRepository) {
        this.bookVersionRepository = bookVersionRepository;
        this.borrowRequestItemRepository = borrowRequestItemRepository;
        this.messageSource = messageSource;
        this.borrowRequestRepository = borrowRequestRepository;
        this.bookRepository = bookRepository;
    }

    private Map<Integer, List<BookVersion>> checkAvailableBooks(Integer bookId) {
        List<BookVersion> availableBooks = bookVersionRepository.findAvailableBooksByBookIds(bookId, BookVersionConstants.AVAILABLE);

        return availableBooks.stream()
                .collect(Collectors.groupingBy(bookVersion -> bookVersion.getBook().getId()));
    }

    private List<BookVersion> availableBooks(Integer bookId,Map<Integer, String> errorMap, int quantityRequested)
    {
        Map<Integer, List<BookVersion>> bookIdToVersions = checkAvailableBooks(bookId);

        List<BookVersion> booksToBorrow = new ArrayList<>();


            List<BookVersion> versions = bookIdToVersions.get(bookId);

            // Nếu không đủ số lượng sách, thêm lỗi vào errorMap
            if (versions == null || versions.size() < quantityRequested) {
                errorMap.put(bookId, "Không đủ sách để mượn cho sách có ID: " + bookId);
            } else {
                booksToBorrow.addAll(versions.subList(0, quantityRequested));
            }
        return booksToBorrow;
    }
    // Kiem tra xem user co duoc phep muon hay không
    private boolean checkConditionBorrrow(User user , Integer sumQuantity, Map<String, String> errorMap)
    {
        if(sumQuantity > 5)
        {
            errorMap.put("Lỗi khi mượn", "Chỉ có thể mượn tối đa 5 cuốn sách");
            return false;
        }
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findByStatusAndUser(
                Arrays.asList(
                PENDING.getValue(),
                OVERDUE.getValue(),
                COMPLETED.getValue()),
                user.getId());
        if(!borrowRequests.isEmpty())
        {
            errorMap.put("Lỗi khi mượn","Vui lòng trả sách để có thể đăng ký mượn tiếp");
            return false;
        }
        return true;
    }

    @Transactional
    public ResponseObject borrowBook(Map<Integer, Integer> bookBorrows, User user)
    {
        int sumQuantity = bookBorrows.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        Map<String, String> errorConditionsMap = new HashMap<>();
        // kiem tra nguoi dung co du dieu kien muon sach hay ko
        if(!checkConditionBorrrow(user, sumQuantity, errorConditionsMap))
        {
            throw new NotEnoughConditionsBorrowException(errorConditionsMap) ;
        }

        else{
            BorrowRequest borrowRequest = new BorrowRequest();
            borrowRequest.setStatus(PENDING.getValue());
            borrowRequest.setCreatedAt(LocalDateTime.now());
            borrowRequest.setUser(user); // chỉ set user thôi, không add vào user.getBorrowRequests()

            Map<Integer, List<BookVersion>> availableBooksMap = new HashMap<>();
            Map<Integer,String>  errorMap = new HashMap<>();

            // Lấy sách khả dụng
            for (Map.Entry<Integer, Integer> entry : bookBorrows.entrySet())
            {
                Integer bookId = entry.getKey();
                Integer quantityRequested = entry.getValue();
                List<BookVersion> availableBooks = availableBooks(bookId, errorMap, quantityRequested);
                availableBooksMap.put(bookId, availableBooks);
            }

            if (!errorMap.isEmpty() || availableBooksMap.isEmpty())
            {

                throw new NotEnoughBookException(errorMap);
            }

            borrowRequest.setQuantity(sumQuantity);
            borrowRequestRepository.save(borrowRequest); // save borrowRequest trước

            // Tạo BorrowRequestItems
            List<BorrowRequestItem> itemsToSave = availableBooksMap.values().stream()
                    .flatMap(versions -> versions.stream()
                            .map(bookVersion -> {
                                BorrowRequestItem item = new BorrowRequestItem();
                                item.setBookVersion(bookVersion);
                                item.setBorrowRequest(borrowRequest);
                                item.setStatus(BRItemStatusConstant.PENDING);
                                item.setCreatedAt(RoleConstants.DATE_TIME);
                                item.setDayExpired(RoleConstants.DATE_TIME.plusDays(3));

                                // update status sách
                                bookVersion.setStatus(BookVersionConstants.RESERVED);

                                return item;
                            })).toList();

            // Save all
            borrowRequestItemRepository.saveAll(itemsToSave);
            bookVersionRepository.saveAll(itemsToSave.stream()
                    .map(BorrowRequestItem::getBookVersion)
                    .collect(Collectors.toList()));

            // Tao Map luu response tra ve
            // Tạo map sách { bookTitle -> quantityRequested }
            Map<String, Integer> bookTitleMap = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : bookBorrows.entrySet()) {
                Integer bookId = entry.getKey();
                Integer quantityRequested = entry.getValue();

                // Lấy title từ bookId
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy sách với id: " + bookId));
                bookTitleMap.put(book.getTitle(), quantityRequested);
            }
            LocalDate timeBorrow = LocalDate.now().plusDays(7);
            BorrowResponse borrowResponse = new BorrowResponse(user.getUsername(),bookTitleMap,sumQuantity,timeBorrow);
            String successMessage = messageSource.getMessage("query.borrow.success", null, Locale.getDefault());
            return new ResponseObject(successMessage, 200, borrowResponse);
        }
    }

    public Page<BorrowRequestSummaryDto> getAllRequests(Integer status, Pageable pageable) {
        Page<BorrowRequestRawDto> rawPage = borrowRequestRepository.findAllByStatus(status, pageable);

        return rawPage.map(raw -> new BorrowRequestSummaryDto(
                raw.id(),
                raw.username(),
                raw.totalBooks(),
                raw.borrowDate(),
                BRStatusConstant.fromValue(raw.status()) // convert int → enum
        ));
    }
}
