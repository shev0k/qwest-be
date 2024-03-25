package com.qwest.backend.service;

import com.qwest.backend.DTO.ExperienceListingDTO;
import java.util.List;
import java.util.Optional;

public interface ExperienceListingService {
    List<ExperienceListingDTO> findAll();
    Optional<ExperienceListingDTO> findById(Long id);
    ExperienceListingDTO save(ExperienceListingDTO dto);
    void deleteById(Long id);
}
