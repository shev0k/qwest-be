package com.qwest.backend.service.impl;

import com.qwest.backend.domain.Author;
import com.qwest.backend.DTO.AuthorDTO;
import com.qwest.backend.mapper.AuthorMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public List<AuthorDTO> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<AuthorDTO> findById(Long id) {
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    @Override
    public AuthorDTO save(AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
