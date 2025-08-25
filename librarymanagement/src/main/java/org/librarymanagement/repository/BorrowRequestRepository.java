package org.librarymanagement.repository;

import org.librarymanagement.entity.BorrowRequest;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {

    @Query("SELECT bri FROM BorrowRequest bri WHERE bri.status IN (:statuses)")
    @EntityGraph(attributePaths = {"user", "borrowRequestItems"})
    List<BorrowRequest> findByStatuses(@Param("statuses") List<Integer> statuses);
}
