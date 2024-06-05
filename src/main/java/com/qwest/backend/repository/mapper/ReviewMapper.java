package com.qwest.backend.repository.mapper;

import com.qwest.backend.domain.Review;
import com.qwest.backend.dto.ReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "stayListingId", source = "stayListing.id")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "authorAvatar", source = "author.avatar")
    @Mapping(target = "stayTitle", source = "stayListing.title")
    ReviewDTO toDto(Review review);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "stayListing", ignore = true)
    Review toEntity(ReviewDTO dto);
}