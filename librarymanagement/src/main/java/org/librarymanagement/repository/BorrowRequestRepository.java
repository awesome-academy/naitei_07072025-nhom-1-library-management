package org.librarymanagement.repository;

import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.entity.BorrowRequest;
import org.librarymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {
    List<BorrowRequest> findBorrowRequestByUser(User user);

    @Query("""
        SELECT new org.librarymanagement.dto.response.BorrowRequestSummaryDto(
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
    Page<BorrowRequestSummaryDto> findAllByStatus(
            @Param("status") Integer status,
            Pageable pageable
    );
}
