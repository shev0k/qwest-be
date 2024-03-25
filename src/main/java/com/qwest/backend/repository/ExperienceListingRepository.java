package com.qwest.backend.repository;

import com.qwest.backend.domain.ExperienceListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceListingRepository extends JpaRepository<ExperienceListing, Long> {
}
