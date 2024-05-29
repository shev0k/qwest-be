package com.qwest.backend.business;

import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.dto.StayListingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorDTO> findAll();
    Optional<AuthorDTO> findById(Long id);

    AuthorDTO update(Long id, AuthorDTO authorDTO);

    AuthorDTO updateAvatar(Long id, MultipartFile avatarFile);

    Optional<AuthorDTO> findByEmail(String email);

    AuthorDTO save(AuthorDTO authorDTO);

    Optional<AuthorDTO> login(AuthorDTO authorDTO);

    void deleteById(Long id);

    AuthorDTO addStayToWishlist(Long authorId, Long stayId);
    AuthorDTO removeStayFromWishlist(Long authorId, Long stayId);

    List<StayListingDTO> getWishlistedStays(Long authorId);
    List<StayListingDTO> getStayListingsByAuthorId(Long authorId);

    AuthorDTO requestHostRole(Long authorId);

    AuthorDTO approveHostRole(Long authorId);

    AuthorDTO rejectHostRole(Long authorId);

    AuthorDTO demoteToTraveler(Long authorId);
}
