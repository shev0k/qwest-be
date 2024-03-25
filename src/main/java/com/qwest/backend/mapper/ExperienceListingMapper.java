package com.qwest.backend.mapper;

import com.qwest.backend.domain.ExperienceListing;
import com.qwest.backend.DTO.ExperienceListingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExperienceListingMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "listingCategory.id", target = "listingCategoryId")
    ExperienceListingDTO toDto(ExperienceListing entity);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "listingCategory", ignore = true)
    ExperienceListing toEntity(ExperienceListingDTO dto);
}
