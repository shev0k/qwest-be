package com.qwest.backend.mapper;

import com.qwest.backend.domain.Author;
import com.qwest.backend.DTO.AuthorDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDTO toDto(Author author);
    Author toEntity(AuthorDTO authorDTO);
}
