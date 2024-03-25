package com.qwest.backend.service;

import com.qwest.backend.DTO.AuthorDTO;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorDTO> findAll();
    Optional<AuthorDTO> findById(Long id);
    AuthorDTO save(AuthorDTO authorDTO);
    void deleteById(Long id);
}
