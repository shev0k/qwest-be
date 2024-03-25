package com.qwest.backend.repository;

import com.qwest.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Custom query methods can be defined here
}
