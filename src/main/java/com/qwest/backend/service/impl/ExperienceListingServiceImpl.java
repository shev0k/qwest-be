package com.qwest.backend.service.impl;

import com.qwest.backend.domain.ExperienceListing;
import com.qwest.backend.DTO.ExperienceListingDTO;
import com.qwest.backend.mapper.ExperienceListingMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.ExperienceListingRepository;
import com.qwest.backend.repository.TaxonomyRepository;
import com.qwest.backend.service.ExperienceListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExperienceListingServiceImpl implements ExperienceListingService {

    private final ExperienceListingRepository experienceListingRepository;
    private final AuthorRepository authorRepository;
    private final TaxonomyRepository taxonomyRepository;
    private final ExperienceListingMapper mapper;

    @Autowired
    public ExperienceListingServiceImpl(ExperienceListingRepository experienceListingRepository,
                                        AuthorRepository authorRepository,
                                        TaxonomyRepository taxonomyRepository,
                                        ExperienceListingMapper mapper) {
        this.experienceListingRepository = experienceListingRepository;
        this.authorRepository = authorRepository;
        this.taxonomyRepository = taxonomyRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ExperienceListingDTO> findAll() {
        return experienceListingRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ExperienceListingDTO> findById(Long id) {
        return experienceListingRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public ExperienceListingDTO save(ExperienceListingDTO dto) {
        ExperienceListing entity = mapper.toEntity(dto);
        entity.setAuthor(authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found")));
        entity.setListingCategory(taxonomyRepository.findById(dto.getListingCategoryId())
                .orElseThrow(() -> new RuntimeException("Taxonomy not found")));
        entity = experienceListingRepository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public void deleteById(Long id) {
        experienceListingRepository.deleteById(id);
    }
}
