package org.librarymanagement.repository;

import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.dto.response.BorrowRequestRawDto;
import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.entity.BorrowRequest;
import org.librarymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {
    List<BorrowRequest> findBorrowRequestByUser(User user);

    @Query("""
        SELECT new org.librarymanagement.dto.response.BorrowRequestRawDto(
            br.id,
            u.username,
            br.quantity,
            br.createdAt,
            br.status
        )
        FROM BorrowRequest br
        JOIN br.user u
        WHERE (:status IS NULL OR br.status = :status)
    """)
    Page<BorrowRequestRawDto> findAllByStatus(
            @Param("status") Integer status,
            Pageable pageable
    );

    @Query("SELECT bri FROM BorrowRequest bri WHERE bri.status IN (:statuses)")
    @EntityGraph(attributePaths = {"user", "borrowRequestItems"})
    List<BorrowRequest> findByStatuses(@Param("statuses") List<Integer> statuses);

    @Query("SELECT bri FROM BorrowRequest bri WHERE bri.user.id = :id AND bri.status IN (:statuses)")
    @EntityGraph(attributePaths = {"user", "borrowRequestItems"})
    List<BorrowRequest> findByStatusAndUser(@Param("statuses") List<Integer> statuses, @Param("id") Integer id);

    @Query("""
        SELECT DISTINCT br FROM BorrowRequest br
        JOIN FETCH br.borrowRequestItems bri
        JOIN FETCH bri.bookVersion bv
        JOIN FETCH bv.book b
        LEFT JOIN FETCH b.bookAuthors ba
        LEFT JOIN FETCH ba.author a
        WHERE br.id = :id
    """)
    Optional<BorrowRequest> findByIdWithDetails(@Param("id") Integer id);

    // Đếm tất cả request tạo sau thời điểm chỉ định
    long countByCreatedAtAfter(LocalDateTime dateTime);
    // Đếm tất cả request tạo trong khoảng
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Đếm request COMPLETED trong tuần này
    long countByStatusAndCreatedAtAfter(Integer status, LocalDateTime dateTime);
    // Đếm request COMPLETED trong tuần trước
    long countByStatusAndCreatedAtBetween(Integer status, LocalDateTime start, LocalDateTime end);// ví dụ đếm request đã APPROVED
}
