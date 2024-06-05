package com.qwest.backend.repository.mapper;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.dto.AuthorDTO;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "stayListingIds", ignore = true)
    @Mapping(target = "wishlistIds", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    @Mapping(target = "jwt", ignore = true)
    AuthorDTO toDto(Author author);

    @AfterMapping
    default void convertStayListingsToIds(Author author, @MappingTarget AuthorDTO dto) {
        Set<Long> stayListingIds = author.getStayListings()
                .stream()
                .map(StayListing::getId)
                .collect(Collectors.toSet());
        dto.setStayListingIds(stayListingIds);

        Set<Long> wishlistIds = author.getWishlist()
                .stream()
                .map(StayListing::getId)
                .collect(Collectors.toSet());
        dto.setWishlistIds(wishlistIds);
    }

    @Mapping(target = "stayListings", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    Author toEntity(AuthorDTO authorDTO);

    @AfterMapping
    default void handleRoleConversion(@MappingTarget Author author, AuthorDTO authorDTO) {
        if (authorDTO.getRole() != null) {
            try {
                author.setRole(AuthorRole.valueOf(authorDTO.getRole()));
            } catch (IllegalArgumentException e) {
                author.setRole(null);
            }
        }
    }
}