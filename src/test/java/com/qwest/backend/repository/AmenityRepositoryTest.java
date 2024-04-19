package com.qwest.backend.repository;

import com.qwest.backend.domain.Amenity;
import com.qwest.backend.domain.util.AmenityCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class AmenityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    void whenSave_thenFindById() {
        // given
        Amenity amenity = new Amenity();
        amenity.setName("Wi-Fi");
        amenity.setCategory(AmenityCategory.GENERAL_AMENITIES);

        // when
        Amenity savedAmenity = amenityRepository.save(amenity);

        // then
        assertNotNull(savedAmenity.getId());
        assertEquals("Wi-Fi", savedAmenity.getName());
        assertEquals(AmenityCategory.GENERAL_AMENITIES, savedAmenity.getCategory());
    }

    @Test
    void whenFindById_thenReturnAmenity() {
        // given
        Amenity amenity = new Amenity();
        amenity.setName("Pool");
        amenity.setCategory(AmenityCategory.LEISURE_RECREATION);
        entityManager.persist(amenity);
        entityManager.flush();

        // when
        Optional<Amenity> foundAmenity = amenityRepository.findById(amenity.getId());

        // then
        assertTrue(foundAmenity.isPresent());
        assertEquals("Pool", foundAmenity.get().getName());
        assertEquals(AmenityCategory.LEISURE_RECREATION, foundAmenity.get().getCategory());
    }

    @Test
    void whenDelete_thenRemoved() {
        // given
        Amenity amenity = new Amenity();
        amenity.setName("Private Parking");
        amenity.setCategory(AmenityCategory.ADDITIONAL_SERVICES);
        Amenity persistAmenity = entityManager.persistFlushFind(amenity);

        // when
        amenityRepository.delete(persistAmenity);
        Optional<Amenity> deletedAmenity = amenityRepository.findById(persistAmenity.getId());

        // then
        assertFalse(deletedAmenity.isPresent());
    }
}
