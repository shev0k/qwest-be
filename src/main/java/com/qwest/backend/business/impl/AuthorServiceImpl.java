package com.qwest.backend.business.impl;

import com.qwest.backend.business.AuthorService;
import com.qwest.backend.business.FileStorageService;
import com.qwest.backend.business.WebSocketNotificationService;
import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.StayListing;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.dto.PasswordResetDTO;
import com.qwest.backend.dto.StayListingDTO;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.StayListingRepository;
import com.qwest.backend.repository.mapper.AuthorMapper;
import com.qwest.backend.repository.mapper.StayListingMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final StayListingRepository stayListingRepository;
    private final StayListingMapper stayListingMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper, StayListingMapper stayListingMapper,
                             PasswordEncoder passwordEncoder, JwtUtil jwtUtil, FileStorageService fileStorageService,
                             StayListingRepository stayListingRepository, WebSocketNotificationService webSocketNotificationService) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.stayListingMapper = stayListingMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.fileStorageService = fileStorageService;
        this.stayListingRepository = stayListingRepository;
        this.webSocketNotificationService = webSocketNotificationService;
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
        if (authorRepository.findByEmail(authorDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        Author author = prepareAuthorEntity(authorDTO);
        author = authorRepository.save(author);
        AuthorDTO savedAuthorDTO = buildAuthorDTOWithJwt(author);
        webSocketNotificationService.broadcastChange("NEW_AUTHOR", savedAuthorDTO);
        return savedAuthorDTO;
    }

    @Override
    public AuthorDTO update(Long id, AuthorDTO authorDTO) {
        String currentUsername = getCurrentUsername();
        Author loggedInUser = authorRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!loggedInUser.getId().equals(id) && loggedInUser.getRole() != AuthorRole.FOUNDER) {
            throw new SecurityException("You do not have permission to update this account.");
        }

        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    boolean isEmailUpdated = !existingAuthor.getEmail().equals(authorDTO.getEmail());

                    if (isEmailUpdated && authorRepository.findByEmail(authorDTO.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("Email already in use.");
                    }

                    updateAuthorFields(existingAuthor, authorDTO);
                    existingAuthor = authorRepository.save(existingAuthor);
                    AuthorDTO responseDto = authorMapper.toDto(existingAuthor);
                    if (isEmailUpdated) {
                        responseDto.setJwt(jwtUtil.generateToken(existingAuthor.getEmail()));
                    }
                    webSocketNotificationService.broadcastChange("UPDATED_AUTHOR", responseDto);
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
        String currentUsername = getCurrentUsername();
        Author loggedInUser = authorRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!loggedInUser.getId().equals(id) && loggedInUser.getRole() != AuthorRole.FOUNDER) {
            throw new SecurityException("You do not have permission to update this account.");
        }

        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    if (avatarFile != null && !avatarFile.isEmpty()) {
                        if (existingAuthor.getAvatar() != null && !existingAuthor.getAvatar().isEmpty()) {
                            fileStorageService.deleteFile(existingAuthor.getAvatar());
                        }
                        String avatarUrl = fileStorageService.uploadFile(avatarFile);
                        existingAuthor.setAvatar(avatarUrl);
                        existingAuthor = authorRepository.save(existingAuthor);
                        AuthorDTO updatedAuthorDTO = authorMapper.toDto(existingAuthor);
                        webSocketNotificationService.broadcastChange("UPDATED_AVATAR", updatedAuthorDTO);
                        return updatedAuthorDTO;
                    } else {
                        throw new IllegalArgumentException("Empty or null avatar file");
                    }
                })
                .orElseThrow(() -> new IllegalStateException(AUTHOR_NOT_FOUND_MSG + id));
    }

    @Override
    public void deleteById(Long id) {
        String currentUsername = getCurrentUsername();
        Author loggedInUser = authorRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (loggedInUser.getRole() != AuthorRole.FOUNDER) {
            throw new SecurityException("You do not have permission to delete this account.");
        }

        authorRepository.deleteById(id);
        webSocketNotificationService.broadcastChange("DELETED_AUTHOR", id);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public AuthorDTO addStayToWishlist(Long authorId, Long stayId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
        StayListing stay = stayListingRepository.findById(stayId)
                .orElseThrow(() -> new EntityNotFoundException("Stay not found with id " + stayId));

        author.getWishlist().add(stay);
        author = authorRepository.save(author);

        return authorMapper.toDto(author);
    }

    @Override
    public AuthorDTO removeStayFromWishlist(Long authorId, Long stayId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
        StayListing stay = stayListingRepository.findById(stayId)
                .orElseThrow(() -> new EntityNotFoundException("Stay not found with id " + stayId));

        author.getWishlist().remove(stay);
        author = authorRepository.save(author);

        return authorMapper.toDto(author);
    }

    @Override
    public List<StayListingDTO> getWishlistedStays(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
        return author.getWishlist().stream()
                .map(stayListingMapper::toDto)
                .toList();
    }

    @Override
    public List<StayListingDTO> getStayListingsByAuthorId(Long authorId) {
        return authorRepository.findById(authorId)
                .map(author -> author.getStayListings().stream()
                        .map(stayListingMapper::toDto)
                        .toList())
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
    }

    @Override
    public AuthorDTO requestHostRole(Long authorId) {
        return authorRepository.findById(authorId)
                .map(author -> {
                    if (author.getRole() != AuthorRole.TRAVELER) {
                        throw new IllegalArgumentException("Only travelers can request to become a host.");
                    }
                    author.setRole(AuthorRole.PENDING_HOST);
                    AuthorDTO updatedAuthorDTO = authorMapper.toDto(authorRepository.save(author));
                    webSocketNotificationService.broadcastChange("REQUESTED_HOST_ROLE", updatedAuthorDTO);
                    return updatedAuthorDTO;
                })
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
    }

    @Override
    public AuthorDTO approveHostRole(Long authorId) {
        return authorRepository.findById(authorId)
                .map(author -> {
                    if (author.getRole() != AuthorRole.PENDING_HOST) {
                        throw new IllegalArgumentException("Only pending hosts can be approved.");
                    }
                    author.setRole(AuthorRole.HOST);
                    AuthorDTO updatedAuthorDTO = authorMapper.toDto(authorRepository.save(author));
                    webSocketNotificationService.broadcastChange("APPROVED_HOST_ROLE", updatedAuthorDTO);
                    return updatedAuthorDTO;
                })
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
    }

    @Override
    public AuthorDTO rejectHostRole(Long authorId) {
        return authorRepository.findById(authorId)
                .map(author -> {
                    if (author.getRole() != AuthorRole.PENDING_HOST) {
                        throw new IllegalArgumentException("Only pending hosts can be rejected.");
                    }
                    author.setRole(AuthorRole.TRAVELER);
                    AuthorDTO updatedAuthorDTO = authorMapper.toDto(authorRepository.save(author));
                    webSocketNotificationService.broadcastChange("REJECTED_HOST_ROLE", updatedAuthorDTO);
                    return updatedAuthorDTO;
                })
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
    }

    @Override
    public AuthorDTO demoteToTraveler(Long authorId) {
        return authorRepository.findById(authorId)
                .map(author -> {
                    if (author.getRole() != AuthorRole.HOST) {
                        throw new IllegalArgumentException("Only hosts can be demoted to traveler.");
                    }
                    author.setRole(AuthorRole.TRAVELER);
                    AuthorDTO updatedAuthorDTO = authorMapper.toDto(authorRepository.save(author));
                    webSocketNotificationService.broadcastChange("DEMOTED_TO_TRAVELER", updatedAuthorDTO);
                    return updatedAuthorDTO;
                })
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_MSG + authorId));
    }

    @Override
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        if (!passwordResetDTO.getNewPassword().equals(passwordResetDTO.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        Author author = authorRepository.findByEmail(passwordResetDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address."));

        author.setPasswordHash(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        authorRepository.save(author);

        webSocketNotificationService.broadcastChange("PASSWORD_RESET", authorMapper.toDto(author));
    }

}
