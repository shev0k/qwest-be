package com.qwest.backend.service.impl;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.Taxonomy;
import com.qwest.backend.DTO.StayListingDTO;
import com.qwest.backend.mapper.StayListingMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.TaxonomyRepository;
import com.qwest.backend.service.StayListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StayListingServiceImpl implements StayListingService {

    private final StayListingRepository stayListingRepository;
    private final AuthorRepository authorRepository;
    private final TaxonomyRepository taxonomyRepository;
    private final StayListingMapper mapper;

    @Autowired
    public StayListingServiceImpl(StayListingRepository stayListingRepository, AuthorRepository authorRepository, TaxonomyRepository taxonomyRepository, StayListingMapper mapper) {
        this.stayListingRepository = stayListingRepository;
        this.authorRepository = authorRepository;
        this.taxonomyRepository = taxonomyRepository;
        this.mapper = mapper;
    }

    @Override
    public List<StayListingDTO> findAllDto() {
        return stayListingRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StayListingDTO> findById(Long id) {
        return stayListingRepository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public StayListingDTO save(StayListingDTO stayListingDTO) {
        StayListing stayListing = mapper.toEntity(stayListingDTO);
        // Set Author
        Author author = authorRepository.findById(stayListingDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        stayListing.setAuthor(author);
        // Set Taxonomy
        Taxonomy taxonomy = taxonomyRepository.findById(stayListingDTO.getListingCategoryId())
                .orElseThrow(() -> new RuntimeException("Taxonomy not found"));
        stayListing.setListingCategory(taxonomy);

        stayListing = stayListingRepository.save(stayListing);
        return mapper.toDto(stayListing);
    }



    @Override
    public void deleteById(Long id) {
        stayListingRepository.deleteById(id);
    }
}
