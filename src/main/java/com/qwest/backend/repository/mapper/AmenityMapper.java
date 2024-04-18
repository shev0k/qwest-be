package com.qwest.backend.repository.mapper;

import com.qwest.backend.dto.AmenityDTO;
import com.qwest.backend.domain.Amenity;
import com.qwest.backend.domain.util.AmenityCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityDTO toDto(Amenity amenity);
    Amenity toEntity(AmenityDTO dto);

    default String map(AmenityCategory value) {
        return value == null ? null : value.name();
    }

    default AmenityCategory map(String value) {
        return value == null ? null : AmenityCategory.valueOf(value);
    }
}