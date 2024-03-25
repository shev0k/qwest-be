package com.qwest.backend.mapper;

import com.qwest.backend.domain.Taxonomy;
import com.qwest.backend.DTO.TaxonomyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaxonomyMapper {
    TaxonomyDTO toDto(Taxonomy taxonomy);
    Taxonomy toEntity(TaxonomyDTO dto);
}

