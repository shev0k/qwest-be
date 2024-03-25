package com.qwest.backend.mapper;

import com.qwest.backend.domain.StayListing;
import com.qwest.backend.DTO.StayListingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StayListingMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "listingCategoryId", source = "listingCategory.id")
    StayListingDTO toDto(StayListing stayListing);

    @Mapping(target = "author", ignore = true) // Set manually in service
    @Mapping(target = "listingCategory", ignore = true) // Set manually in service
    StayListing toEntity(StayListingDTO dto);
}
