package com.qwest.backend.service.impl;

import com.qwest.backend.domain.Taxonomy;
import com.qwest.backend.DTO.TaxonomyDTO;
import com.qwest.backend.mapper.TaxonomyMapper;
import com.qwest.backend.repository.TaxonomyRepository;
import com.qwest.backend.service.TaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

    private final TaxonomyRepository taxonomyRepository;
    private final TaxonomyMapper mapper;

    @Autowired
    public TaxonomyServiceImpl(TaxonomyRepository taxonomyRepository, TaxonomyMapper mapper) {
        this.taxonomyRepository = taxonomyRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TaxonomyDTO> findAllDto() {
        return taxonomyRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaxonomyDTO> findById(Long id) {
        return taxonomyRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public TaxonomyDTO save(TaxonomyDTO taxonomyDTO) {
        Taxonomy taxonomy = mapper.toEntity(taxonomyDTO);
        taxonomy = taxonomyRepository.save(taxonomy);
        return mapper.toDto(taxonomy);
    }

    @Override
    public void deleteById(Long id) {
        taxonomyRepository.deleteById(id);
    }
}
