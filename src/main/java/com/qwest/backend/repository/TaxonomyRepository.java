package com.qwest.backend.repository;

import com.qwest.backend.domain.Taxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long> {
}
