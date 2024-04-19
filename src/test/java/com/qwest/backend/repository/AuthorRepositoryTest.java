package com.qwest.backend.repository;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void whenFindByEmail_thenReturnAuthor() {
        // given
        Author author = new Author();
        author.setEmail("john.doe@example.com");
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setUsername("johndoe");
        author.setRole(AuthorRole.TRAVELER); // Example role
        entityManager.persist(author);
        entityManager.flush();

        // when
        Optional<Author> found = authorRepository.findByEmail("john.doe@example.com");

        // then
        assertTrue(found.isPresent(), "Author should be found");
        assertEquals("John", found.get().getFirstName(), "The first name should match");
    }

    @Test
    void whenFindByEmail_NotFound_thenReturnEmpty() {
        // when
        Optional<Author> found = authorRepository.findByEmail("nonexistent@example.com");

        // then
        assertFalse(found.isPresent(), "Author should not be found");
    }

    @Test
    void whenSaveAuthor_thenReturnSavedAuthor() {
        // given
        Author author = new Author();
        author.setEmail("jane.doe@example.com");
        author.setFirstName("Jane");
        author.setLastName("Doe");
        author.setUsername("janedoe");
        author.setRole(AuthorRole.FOUNDER); // Example role
        author.setDescription("Example description");

        // when
        Author savedAuthor = authorRepository.save(author);

        // then
        assertNotNull(savedAuthor.getId(), "Author should have an ID after saving");
        assertEquals("Jane", savedAuthor.getFirstName(), "The first name should match");
        assertEquals("Example description", savedAuthor.getDescription(), "The description should match");
    }

    @Test
    void whenDeleteAuthor_thenRemovedFromDatabase() {
        // given
        Author author = new Author();
        author.setEmail("delete@example.com");
        author.setFirstName("Delete");
        author.setLastName("Me");
        author.setUsername("deleteme");
        entityManager.persist(author);
        entityManager.flush();

        // when
        authorRepository.delete(author);
        Optional<Author> found = authorRepository.findById(author.getId());

        // then
        assertFalse(found.isPresent(), "Author should be deleted");
    }
}