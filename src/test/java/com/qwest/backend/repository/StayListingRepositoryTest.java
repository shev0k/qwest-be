package com.qwest.backend.repository;

import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.PropertyType;
import com.qwest.backend.domain.util.RentalFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class StayListingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StayListingRepository stayListingRepository;

    @Test
    void whenSave_thenReturnSavedStayListing() {
        // given
        StayListing stayListing = new StayListing();
        stayListing.setTitle("Lovely Cottage");
        stayListing.setDate(LocalDate.now());
        stayListing.setPropertyType(PropertyType.APARTMENT);
        stayListing.setRentalFormType(RentalFormType.ENTIRE_PLACE);

        // when
        StayListing savedListing = stayListingRepository.save(stayListing);

        // then
        assertNotNull(savedListing.getId());
        assertEquals("Lovely Cottage", savedListing.getTitle());
    }

    @Test
    void whenFindById_thenReturnStayListing() {
        // given
        StayListing stayListing = new StayListing();
        stayListing.setTitle("Beachfront Villa");
        stayListing = entityManager.persistAndFlush(stayListing);

        // when
        Optional<StayListing> foundListing = stayListingRepository.findById(stayListing.getId());

        // then
        assertTrue(foundListing.isPresent());
        assertEquals("Beachfront Villa", foundListing.get().getTitle());
    }

    @Test
    void whenDelete_thenRemoveStayListing() {
        // given
        StayListing stayListing = new StayListing();
        stayListing.setTitle("Mountain Retreat");
        StayListing persistedListing = entityManager.persistAndFlush(stayListing);

        // when
        stayListingRepository.delete(persistedListing);
        Optional<StayListing> deletedListing = stayListingRepository.findById(persistedListing.getId());

        // then
        assertFalse(deletedListing.isPresent());
    }
}
