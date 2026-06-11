package com.example.devsecopslab.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserSearchRepository {

    private final EntityManager entityManager;

    /**
     * VULNERABLE:
     * SQL Injection via string concatenation.
     */
    public List<User> searchByEmailVulnerable(String email) {
        String sql = "SELECT * FROM users WHERE email = '" + email + "'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    /**
     * FIXED:
     * Parameter binding prevents SQL Injection.
     */
    public List<User> searchByEmailFixed(String email) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM users WHERE email = :email",
                User.class
        );
        query.setParameter("email", email);
        return query.getResultList();
    }

    /**
     * VULNERABLE:
     * Dynamic ORDER BY injection.
     */
    public List<User> findAllWithSortVulnerable(String sortBy) {
        String sql = "SELECT * FROM users ORDER BY " + sortBy;
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }
}
