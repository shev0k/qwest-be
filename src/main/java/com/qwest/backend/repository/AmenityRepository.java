package com.qwest.backend.repository;

import com.qwest.backend.domain.util.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}