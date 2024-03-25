package com.qwest.backend.mapper;

import com.qwest.backend.domain.Post;
import com.qwest.backend.DTO.PostDTO;
import com.qwest.backend.domain.Taxonomy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "categoriesToIds")
    PostDTO toDto(Post post);

    @Mapping(target = "author", ignore = true) // Handle in service layer
    @Mapping(target = "categories", ignore = true) // Handle in service layer
    Post toEntity(PostDTO dto);

    // Helper method to convert categories to a list of their IDs
    @Named("categoriesToIds")
    static List<Long> categoriesToIds(Set<Taxonomy> categories) {
        return categories.stream().map(Taxonomy::getId).collect(Collectors.toList());
    }
}
