package com.qwest.backend.repository;

import com.qwest.backend.domain.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}