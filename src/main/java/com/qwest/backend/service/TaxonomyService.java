package com.qwest.backend.service;

import com.qwest.backend.DTO.TaxonomyDTO;
import java.util.List;
import java.util.Optional;

public interface TaxonomyService {
    List<TaxonomyDTO> findAllDto();
    Optional<TaxonomyDTO> findById(Long id);
    TaxonomyDTO save(TaxonomyDTO taxonomyDTO);
    void deleteById(Long id);
}
