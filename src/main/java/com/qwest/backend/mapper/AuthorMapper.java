package com.qwest.backend.mapper;

import com.qwest.backend.domain.Author;
import com.qwest.backend.DTO.AuthorDTO;
import com.qwest.backend.domain.StayListing;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AuthorMapper {

    @Mappings({
            @Mapping(target = "stayListingIds", ignore = true)
    })
    public abstract AuthorDTO toDto(Author author);

    @AfterMapping
    protected void convertStayListingsToIds(Author author, @MappingTarget AuthorDTO dto) {
        Set<Long> stayListingIds = author.getStayListings()
                .stream()
                .map(StayListing::getId)
                .collect(Collectors.toSet());
        dto.setStayListingIds(stayListingIds);
    }

    @Mappings({
            @Mapping(target = "stayListings", ignore = true)
    })
    public abstract Author toEntity(AuthorDTO authorDTO);

}
