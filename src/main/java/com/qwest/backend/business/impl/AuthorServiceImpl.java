package com.qwest.backend.business.impl;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.domain.Author;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.repository.mapper.AuthorMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.business.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<AuthorDTO> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).toList();
    }

    @Override
    public Optional<AuthorDTO> findById(Long id) {
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    @Override
    public AuthorDTO save(AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        if (author.getId() == null) {
            author.setRole(AuthorRole.TRAVELER);
        }
        if (authorDTO.getPassword() != null && !authorDTO.getPassword().isEmpty()) {
            author.setPasswordHash(passwordEncoder.encode(authorDTO.getPassword()));
        }
        author = authorRepository.save(author);
        AuthorDTO savedDto = authorMapper.toDto(author);
        String jwt = jwtUtil.generateToken(savedDto.getEmail());
        savedDto.setJwt(jwt);
        return savedDto;
    }


    @Override
    public Optional<AuthorDTO> findByEmail(String email) {
        return authorRepository.findByEmail(email).map(authorMapper::toDto);
    }
    @Override
    public Optional<AuthorDTO> login(AuthorDTO authorDTO) {
        Optional<Author> foundAuthor = authorRepository.findByEmail(authorDTO.getEmail());
        if (foundAuthor.isPresent() && passwordEncoder.matches(authorDTO.getPassword(), foundAuthor.get().getPasswordHash())) {
            AuthorDTO dto = authorMapper.toDto(foundAuthor.get());
            String jwt = jwtUtil.generateToken(dto.getEmail());
            dto.setJwt(jwt);
            return Optional.of(dto);
        }
        return Optional.empty();
    }


    @Override
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
