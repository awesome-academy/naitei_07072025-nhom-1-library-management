package org.librarymanagement.repository;

import org.librarymanagement.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    boolean existsUserByEmail(String email);
    boolean existsUserByPhone(String phone);
    boolean existsUserByUsername(String username);
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
}
