package com.eska.evenity.repository;

import com.eska.evenity.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM UserCredential u WHERE MONTH(u.createdDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(u.createdDate) = YEAR(CURRENT_DATE) AND u.username <> 'admin@gmail.com'")
    Integer countUsersRegisteredThisMonth();
}
