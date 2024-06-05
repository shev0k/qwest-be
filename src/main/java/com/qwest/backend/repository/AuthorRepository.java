package com.qwest.backend.repository;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmail(String email);

    List<Author> findByRole(AuthorRole role);
}
