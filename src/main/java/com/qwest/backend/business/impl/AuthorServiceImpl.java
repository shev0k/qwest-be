package com.qwest.backend.business.impl;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.domain.Author;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.repository.mapper.AuthorMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.business.AuthorService;
import com.qwest.backend.business.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final String AUTHOR_NOT_FOUND_MSG = "Author not found with id ";

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper,
                             PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                             FileStorageService fileStorageService) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<AuthorDTO> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();
    }

    @Override
    public Optional<AuthorDTO> findById(Long id) {
        return Optional.ofNullable(authorRepository.findById(id)
                .map(authorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + id)));
    }

    @Override
    public AuthorDTO save(AuthorDTO authorDTO) {
        Author author = prepareAuthorEntity(authorDTO);
        author = authorRepository.save(author);
        return buildAuthorDTOWithJwt(author);
    }

    @Override
    public AuthorDTO update(Long id, AuthorDTO authorDTO) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    boolean isEmailUpdated = !existingAuthor.getEmail().equals(authorDTO.getEmail());
                    updateAuthorFields(existingAuthor, authorDTO);
                    existingAuthor = authorRepository.save(existingAuthor);
                    AuthorDTO responseDto = authorMapper.toDto(existingAuthor);
                    if (isEmailUpdated) {
                        responseDto.setJwt(jwtUtil.generateToken(existingAuthor.getEmail())); // Generate new token on email change
                    }
                    return responseDto;
                })
                .orElseThrow(() -> new IllegalStateException(AUTHOR_NOT_FOUND_MSG + id));
    }

    private Author prepareAuthorEntity(AuthorDTO authorDTO) {
        Author author = authorMapper.toEntity(authorDTO);
        if (author.getId() == null) {
            author.setRole(AuthorRole.TRAVELER);
        }
        if (authorDTO.getPassword() != null && !authorDTO.getPassword().isEmpty()) {
            author.setPasswordHash(passwordEncoder.encode(authorDTO.getPassword()));
        }
        return author;
    }

    private void updateAuthorFields(Author existingAuthor, AuthorDTO authorDTO) {
        Optional.ofNullable(authorDTO.getFirstName()).ifPresent(existingAuthor::setFirstName);
        Optional.ofNullable(authorDTO.getLastName()).ifPresent(existingAuthor::setLastName);
        Optional.ofNullable(authorDTO.getUsername()).ifPresent(existingAuthor::setUsername);
        Optional.ofNullable(authorDTO.getEmail()).ifPresent(existingAuthor::setEmail);
        Optional.ofNullable(authorDTO.getCountry()).ifPresent(existingAuthor::setCountry);
        Optional.ofNullable(authorDTO.getPhoneNumber()).ifPresent(existingAuthor::setPhoneNumber);
        Optional.ofNullable(authorDTO.getDescription()).ifPresent(existingAuthor::setDescription);
        Optional.ofNullable(authorDTO.getRole()).map(AuthorRole::valueOf).ifPresent(existingAuthor::setRole);
        Optional.ofNullable(authorDTO.getPassword()).filter(pwd -> !pwd.isEmpty() && !passwordEncoder.matches(pwd, existingAuthor.getPasswordHash()))
                .ifPresent(pwd -> existingAuthor.setPasswordHash(passwordEncoder.encode(pwd)));
    }

    private AuthorDTO buildAuthorDTOWithJwt(Author author) {
        AuthorDTO dto = authorMapper.toDto(author);
        dto.setJwt(jwtUtil.generateToken(dto.getEmail()));
        return dto;
    }

    @Override
    public Optional<AuthorDTO> findByEmail(String email) {
        return authorRepository.findByEmail(email)
                .map(authorMapper::toDto);
    }

    @Override
    public Optional<AuthorDTO> login(AuthorDTO authorDTO) {
        return authorRepository.findByEmail(authorDTO.getEmail())
                .filter(foundAuthor -> passwordEncoder.matches(authorDTO.getPassword(), foundAuthor.getPasswordHash()))
                .map(foundAuthor -> {
                    AuthorDTO dto = authorMapper.toDto(foundAuthor);
                    dto.setJwt(jwtUtil.generateToken(dto.getEmail()));
                    return dto;
                });
    }

    @Override
    public AuthorDTO updateAvatar(Long id, MultipartFile avatarFile) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    if (avatarFile != null && !avatarFile.isEmpty()) {
                        if (existingAuthor.getAvatar() != null && !existingAuthor.getAvatar().isEmpty()) {
                            fileStorageService.deleteFile(existingAuthor.getAvatar());
                        }
                        String avatarUrl = fileStorageService.uploadFile(avatarFile);
                        existingAuthor.setAvatar(avatarUrl);
                        existingAuthor = authorRepository.save(existingAuthor);
                        return authorMapper.toDto(existingAuthor);
                    } else {
                        throw new IllegalArgumentException("Empty or null avatar file");
                    }
                })
                .orElseThrow(() -> new IllegalStateException(AUTHOR_NOT_FOUND_MSG + id));
    }

    @Override
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }
}
