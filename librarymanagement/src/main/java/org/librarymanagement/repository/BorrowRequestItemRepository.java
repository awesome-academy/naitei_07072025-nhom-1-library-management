package org.librarymanagement.repository;

import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BorrowRequest;
import org.librarymanagement.entity.BorrowRequestItem;
import org.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowRequestItemRepository extends JpaRepository<BorrowRequestItem, Integer> {

    @Query("""
        SELECT COUNT(bri) > 0 FROM BorrowRequestItem bri
        WHERE bri.borrowRequest.user = :user AND bri.bookVersion.book = :book
          AND bri.status = :status
    """)
    boolean existsByBookAndUserAndStatus(@Param("book") Book book, @Param("user") User user, @Param("status") int status);

    @Query("SELECT bri FROM BorrowRequestItem bri WHERE bri.status IN (:statuses)")
    List<BorrowRequestItem> findByStatuses(@Param("statuses") List<Integer> statuses);
    List<BorrowRequestItem> findBorrowRequestItemByBorrowRequest(BorrowRequest borrowRequest);

    @Query("""
            SELECT bri FROM BorrowRequestItem bri
            JOIN FETCH bri.bookVersion bv
            JOIN FETCH bv.book b
            JOIN bri.borrowRequest br
            WHERE bri.borrowRequest.user.id = :userId
            AND br.status = :requestStatusCompleted
            AND bri.status IN :itemStatusList
            ORDER BY bri.createdAt DESC
            """)
    List<BorrowRequestItem> findBorrowHistoryByUserId(
            @Param("userId") Integer userId,
            @Param("requestStatusCompleted") int requestStatusCompleted,
            @Param("itemStatusList") List<Integer> itemStatusList
    );
}
