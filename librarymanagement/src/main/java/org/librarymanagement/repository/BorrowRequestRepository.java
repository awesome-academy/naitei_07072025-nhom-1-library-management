package org.librarymanagement.repository;

import org.librarymanagement.dto.response.BorrowRequestRawDto;
import lombok.NonNull;
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

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"user", "borrowRequestItems"})
    Optional<BorrowRequest> findById(@NonNull Integer id);
}
