package org.librarymanagement.repository;

import org.librarymanagement.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Integer> {
    Optional<UserInteraction> findByUserIdAndTargetIdAndTargetTypeAndAction(Integer userId, Integer targetId, String targetType, String action);
    void deleteByUserIdAndTargetIdAndTargetTypeAndAction(Integer userId, Integer targetId, String targetType, String action);
}
