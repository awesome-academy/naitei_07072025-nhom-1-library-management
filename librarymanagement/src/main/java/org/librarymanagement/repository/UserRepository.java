package org.librarymanagement.repository;

import org.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    boolean existsUserByEmail(String email);
    boolean existsUserByPhone(String phone);
    boolean existsUserByUsername(String username);
    Optional<User> findByUsername(String username);
    User findByEmail(String email);

    User findUserById(Integer id);
    @Query(value = """
        SELECT YEAR(u.created_at) as year, MONTH(u.created_at) as month, COUNT(*) as count
        FROM users u
        WHERE u.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
        GROUP BY YEAR(u.created_at), MONTH(u.created_at)
        ORDER BY YEAR(u.created_at), MONTH(u.created_at)
        """, nativeQuery = true)
    List<Object[]> countNewUsersLast12Months();
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);// số member mới
}
