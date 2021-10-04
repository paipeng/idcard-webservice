package com.paipeng.idcard.repository;

import com.paipeng.idcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    @Query("SELECT u FROM User u WHERE u.token is not null")
    List<User> findUsersWithToken();
}
