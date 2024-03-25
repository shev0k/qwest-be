package com.qwest.backend.repository;

import com.qwest.backend.domain.StayListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StayListingRepository extends JpaRepository<StayListing, Long> {
}
